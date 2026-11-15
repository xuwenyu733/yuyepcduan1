package cn.iocoder.yudao.module.fish.service.smartentry;

import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.security.core.LoginUser;
import cn.iocoder.yudao.framework.tenant.core.context.TenantContextHolder;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpond.ZkPondDO;
import cn.iocoder.yudao.module.fish.service.zkpond.ZkPondService;
import org.junit.jupiter.api.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;
import javax.sql.DataSource;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;

class SmartEntryServiceTest {
    private JdbcTemplate jdbc;
    private ZkPondService ponds;
    private SmartEntryService service;

    @BeforeEach void setUp() {
        jdbc = mock(JdbcTemplate.class); ponds = mock(ZkPondService.class);
        service = new SmartEntryService(mock(DataSource.class), ponds);
        ReflectionTestUtils.setField(service, "jdbc", jdbc);
        LoginUser user = new LoginUser(); user.setId(123L); user.setTenantId(1L);
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(user, null, List.of()));
        TenantContextHolder.setTenantId(1L);
    }
    @AfterEach void clear() { TenantContextHolder.clear(); SecurityContextHolder.clearContext(); }

    @Test void retainsSingleChineseKeywordsAndRemovesQueryBoilerplate() {
        assertEquals(List.of("虾"), SmartEntryService.searchTerms("虾"));
        assertEquals(List.of("鱼"), SmartEntryService.searchTerms("查询鱼"));
        assertTrue(SmartEntryService.searchTerms("%%%!!!").isEmpty());
    }
    @Test void meaninglessQueryDoesNotReturnRecentUnrelatedRows() {
        assertEquals(List.of(), service.search("%%%!!!").get("hits"));
        verifyNoInteractions(jdbc, ponds);
    }
    @Test void crossTenantAccessIsRejectedBeforeDatabaseReads() {
        TenantContextHolder.setTenantId(2L);
        ServiceException error = assertThrows(ServiceException.class, () -> service.list(1, 10));
        assertEquals(403, error.getCode()); verifyNoInteractions(jdbc, ponds);
    }
    @Test void refusesScriptsDisguisedAsImages() {
        assertThrows(ServiceException.class, () -> SmartEntryService.detectImageType("<svg onload='alert(1)'/>".getBytes()));
        byte[] png = Base64.getDecoder().decode("iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mP8/x8AAusB9Y9ZK9sAAAAASUVORK5CYII=");
        assertEquals("image/png", SmartEntryService.detectImageType(png));
    }
    @SuppressWarnings("unchecked")
    @Test void pondAnswerUsesDepartmentFilteredServiceResultsAndTotal() {
        when(jdbc.query(anyString(), any(RowMapper.class), any(Object[].class))).thenReturn(List.of());
        when(jdbc.queryForObject(anyString(), eq(Long.class), any(Object[].class))).thenReturn(0L);
        ZkPondDO visible = new ZkPondDO(); visible.setPondName("当前部门鱼池");
        when(ponds.getZkPondPage(any())).thenReturn(new PageResult<>(List.of(visible), 1L));
        var hits = (List<Map<String, Object>>) service.search("鱼池总数").get("hits");
        String content = hits.get(0).get("content").toString();
        assertTrue(content.contains("当前可见鱼池总数：1"));
        assertTrue(content.contains("当前部门鱼池"));
        verify(ponds).getZkPondPage(argThat(page -> page.getPageNo() == 1 && page.getPageSize() == 10));
        verify(jdbc, never()).queryForList(contains("zk_pond"), any(Object[].class));
        verify(jdbc, never()).queryForObject(contains("zk_pond"), eq(Long.class), any(Object[].class));
    }
}
