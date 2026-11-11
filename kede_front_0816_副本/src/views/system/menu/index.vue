<template>
  <div class="app-container">
    <doc-alert v-if="false" title="功能权限" url="https://doc.iocoder.cn/resource-permission" />
    <doc-alert v-if="false" title="菜单路由" url="https://doc.iocoder.cn/vue2/route/" />
    <!-- 搜索工作栏 -->
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch">
      <el-form-item label="菜单名称" prop="name">
        <el-input v-model="queryParams.name" placeholder="请输入菜单名称" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="菜单状态" clearable>
          <el-option v-for="dict in statusDictDatas" :key="parseInt(dict.value)" :label="dict.label" :value="parseInt(dict.value)"/>
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd"
                   v-hasPermi="['system:menu:create']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="info" plain icon="el-icon-sort" size="mini" @click="toggleExpandAll">展开/折叠</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-s-check"
          size="mini"
          @click="syncVisibleMenusToRoles"
          v-hasPermi="['system:menu:update']"
        >同步可见菜单授权</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="isEmpty(checkedIds)"
          @click="handleDeleteBatch"
          v-hasPermi="['system:menu:delete']"
        >
          批量删除
        </el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-if="refreshTable" v-loading="loading" :data="menuList" row-key="id" :default-expand-all="isExpandAll"
              :tree-props="{children: 'children', hasChildren: 'hasChildren'}" @selection-change="handleRowCheckboxChange">
      <el-table-column type="selection" width="55"/>
      <el-table-column prop="name" label="菜单名称" :show-overflow-tooltip="true" width="250"></el-table-column>
      <el-table-column prop="icon" label="图标" align="center" width="100">
        <template v-slot="scope">
          <svg-icon v-if="scope.row.icon" :icon-class="scope.row.icon" />
        </template>
      </el-table-column>
      <el-table-column prop="sort" label="排序" width="60"></el-table-column>
      <el-table-column prop="permission" label="权限标识" :show-overflow-tooltip="true" />
      <el-table-column prop="component" label="组件路径" :show-overflow-tooltip="true" />
      <el-table-column prop="componentName" label="组件名称" :show-overflow-tooltip="true" />
      <el-table-column prop="status" label="状态" width="80">
        <template v-slot="scope">
          <dict-tag :type="DICT_TYPE.COMMON_STATUS" :value="scope.row.status"/>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template v-slot="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)"
                     v-hasPermi="['system:menu:update']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-plus" @click="handleAdd(scope.row)"
                     v-hasPermi="['system:menu:create']">新增</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
                     v-hasPermi="['system:menu:delete']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 添加或修改菜单对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="800px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="24">
            <el-form-item label="上级菜单">
              <treeselect v-model="form.parentId" :options="menuOptions" :normalizer="normalizer" :show-count="true"
                          placeholder="选择上级菜单"/>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="菜单类型" prop="type">
              <el-radio-group v-model="form.type">
                <el-radio v-for="dict in menuTypeDictDatas" :key="parseInt(dict.value)" :label="parseInt(dict.value)">
                  {{dict.label}}</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item v-if="form.type !== 3" label="菜单图标">
              <el-popover placement="bottom-start" width="460" trigger="click" @show="$refs['iconSelect'].reset()">
                <IconSelect ref="iconSelect" @selected="selected" />
                <el-input slot="reference" v-model="form.icon" placeholder="点击选择图标" readonly>
                  <svg-icon v-if="form.icon" slot="prefix" :icon-class="form.icon" class="el-input__icon"
                            style="height: 32px;width: 16px;"/>
                  <i v-else slot="prefix" class="el-icon-search el-input__icon" />
                </el-input>
              </el-popover>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="菜单名称" prop="name">
              <el-input v-model="form.name" placeholder="请输入菜单名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="显示排序" prop="sort">
              <el-input-number v-model="form.sort" controls-position="right" :min="0" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item v-if="form.type !== 3" label="路由地址" prop="path">
              <span slot="label">
                <el-tooltip content="访问的路由地址，如：`user`。如需外网地址时，则以 `http(s)://` 开头" placement="top">
                <i class="el-icon-question" />
                </el-tooltip>
                路由地址
              </span>
              <el-input v-model="form.path" placeholder="请输入路由地址" />
            </el-form-item>
          </el-col>
					<el-col :span="12">
						<el-form-item v-if="form.type !== 1" label="权限标识">
              <span slot="label">
                <el-tooltip content="Controller 方法上的权限字符，如：@PreAuthorize(`@ss.hasPermission('system:user:list')`)" placement="top">
                  <i class="el-icon-question" />
                </el-tooltip>
                权限字符
              </span>
							<el-input v-model="form.permission" placeholder="请权限标识" maxlength="50" />
						</el-form-item>
					</el-col>
          <el-col :span="12" v-if="form.type === 2">
            <el-form-item label="组件路径" prop="component">
              <el-input v-model="form.component" placeholder="例如说：system/user/index" />
            </el-form-item>
          </el-col>
					<el-col :span="12" v-if="form.type === 2">
						<el-form-item label="组件名称" prop="componentName">
							<el-input v-model="form.componentName" placeholder="例如说：SystemUser" />
						</el-form-item>
					</el-col>
          <el-col :span="12">
            <el-form-item label="菜单状态" prop="status">
              <span slot="label">
                <el-tooltip content="选择停用时，路由将不会出现在侧边栏，也不能被访问" placement="top">
                  <i class="el-icon-question" />
                </el-tooltip>
                菜单状态
              </span>
              <el-radio-group v-model="form.status">
                <el-radio v-for="dict in this.getDictDatas(DICT_TYPE.COMMON_STATUS)"
                          :key="dict.value" :label="parseInt(dict.value)">{{dict.label}}</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item v-if="form.type !== 3" label="显示状态">
              <span slot="label">
                <el-tooltip content="选择隐藏时，路由将不会出现在侧边栏，但仍然可以访问" placement="top">
                  <i class="el-icon-question" />
                </el-tooltip>
                是否显示
              </span>
              <el-radio-group v-model="form.visible">
                <el-radio :key="true" :label="true">显示</el-radio>
                <el-radio :key="false" :label="false">隐藏</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item v-if="form.type === 2" label="开放普通用户">
              <span slot="label">
                <el-tooltip content="勾选后，该单个页面菜单会授权给业务角色（科德-管理员 / common / 组成员 / 组长），便于单独开放某一页" placement="top">
                  <i class="el-icon-question" />
                </el-tooltip>
                开放普通用户
              </span>
              <el-switch v-model="form.openToCommonUsers" active-text="开放" inactive-text="不开放" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item v-if="form.type !== 3" label="总是显示">
              <span slot="label">
                <el-tooltip content="选择不是时，当该菜单只有一个子菜单时，不展示自己，直接展示子菜单" placement="top">
                  <i class="el-icon-question" />
                </el-tooltip>
                 总是显示
              </span>
              <el-radio-group v-model="form.alwaysShow">
                <el-radio :key="true" :label="true">总是</el-radio>
                <el-radio :key="false" :label="false">不是</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
					<el-col :span="12">
						<el-form-item v-if="form.type === 2" label="是否缓存">
              <span slot="label">
                <el-tooltip content="选择缓存时，则会被 `keep-alive` 缓存，必须填写「组件名称」字段" placement="top">
                  <i class="el-icon-question" />
                </el-tooltip>
                 是否缓存
              </span>
							<el-radio-group v-model="form.keepAlive">
								<el-radio :key="true" :label="true">缓存</el-radio>
								<el-radio :key="false" :label="false">不缓存</el-radio>
							</el-radio-group>
						</el-form-item>
					</el-col>
        </el-row>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listMenu, getMenu, delMenu, addMenu, updateMenu, delMenuList } from "@/api/system/menu";
import { listUserRoles, listRoleMenus, assignRoleMenu } from "@/api/system/permission";
import { listSimpleRoles } from "@/api/system/role";
import Treeselect from "@riophae/vue-treeselect";
import "@riophae/vue-treeselect/dist/vue-treeselect.css";
import IconSelect from "@/components/IconSelect";

import { SystemMenuTypeEnum, CommonStatusEnum } from '@/utils/constants'
import { getDictDatas, DICT_TYPE } from '@/utils/dict'
import {isExternal} from "@/utils/validate";

/** 需自动补齐的嵌入式子应用菜单（挂在「综合模块」下，与鱼病/实验同级，带侧栏 Layout） */
const EMBEDDED_MENUS = [
  {
    name: '风味优化管理',
    path: 'flavorOptimize',
    component: 'iot/flavorOptimize',
    componentName: 'FlavorOptimize',
    icon: 'guide',
    sort: 4
  },
  {
    name: '阈值设置',
    path: 'threshold-settings',
    component: 'iot/thresholdSettings',
    componentName: 'ThresholdSettings',
    icon: 'guide',
    sort: 5
  }
]

const EMBEDDED_PARENT_PATH = '/experimental' // 综合模块

/** 新增菜单后自动授权的业务角色白名单（避免下发到财务/CRM/测试等） */
const AUTO_ASSIGN_ROLE_CODES = [
  'common',
  'ke_admin', // 科德-管理员（层级较高的业务账号，须纳入「开放普通用户」）
  'kede_group_member',
  'kede_group_leader'
]

/** 按名称兜底匹配（防止角色 code 变更后开放失效） */
const AUTO_ASSIGN_ROLE_NAMES = [
  '普通角色',
  '科德-管理员',
  '科德-养殖人员',
  '科德-养殖负责人'
]

function isAutoAssignRole(role = {}) {
  const code = String(role.code || role.roleCode || '').toLowerCase()
  if (AUTO_ASSIGN_ROLE_CODES.includes(code)) return true
  const name = String(role.name || role.roleName || '').trim()
  return AUTO_ASSIGN_ROLE_NAMES.includes(name)
}

/** 敏感系统菜单不自动下发（路由/组件以 system 开头） */
function isSensitiveSystemMenu(form = {}) {
  const path = String(form.path || '').replace(/^\//, '').toLowerCase()
  const component = String(form.component || '').replace(/^\//, '').toLowerCase()
  const permission = String(form.permission || '').toLowerCase()
  if (path === 'system' || path.startsWith('system/')) return true
  if (component.startsWith('system/')) return true
  if (permission.startsWith('system:')) return true
  return false
}

export default {
  name: "SystemMenu",
  components: { Treeselect, IconSelect },
  data() {
    return {
      // 遮罩层
      loading: true,
      // 显示搜索条件
      showSearch: true,
      // 菜单表格树数据
      menuList: [],
      // 菜单树选项
      menuOptions: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 是否展开，默认全部折叠
      isExpandAll: false,
      // 重新渲染表格状态
      refreshTable: true,
      // 选中行
      checkedIds: [],
      // 查询参数
      queryParams: {
        name: undefined,
        visible: undefined
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        name: [
          { required: true, message: "菜单名称不能为空", trigger: "blur" }
        ],
        sort: [
          { required: true, message: "菜单顺序不能为空", trigger: "blur" }
        ],
        path: [
          { required: true, message: "路由地址不能为空", trigger: "blur" }
        ],
        status: [
          { required: true, message: "状态不能为空", trigger: "blur" }
        ]
      },

      // 枚举
      MenuTypeEnum: SystemMenuTypeEnum,
      CommonStatusEnum: CommonStatusEnum,
      // 数据字典
      menuTypeDictDatas: getDictDatas(DICT_TYPE.SYSTEM_MENU_TYPE),
      statusDictDatas: getDictDatas(DICT_TYPE.COMMON_STATUS)
    };
  },
  created() {
    this.getList().then(() => this.ensureEmbeddedMenus());
  },
  methods: {
    // 选择图标
    selected(name) {
      this.form.icon = name;
    },
    /** 自动补齐风味 / 阈值菜单（挂综合模块；鱼病 / 实验已手动添加则跳过） */
    async ensureEmbeddedMenus() {
      const FLAG = 'EMBEDDED_MENUS_ENSURED_V5';
      if (sessionStorage.getItem(FLAG)) return;
      try {
        const response = await listMenu();
        const list = response.data || [];
        const parent = list.find(m =>
          m.path === EMBEDDED_PARENT_PATH ||
          m.path === 'experimental' ||
          m.name === '综合模块'
        );
        const parentId = parent && parent.id != null ? parent.id : 0;
        const created = [];
        for (const item of EMBEDDED_MENUS) {
          const exists = list.some(m =>
            m.component === item.component ||
            m.component === '/' + item.component ||
            m.path === item.path ||
            m.path === '/' + item.path ||
            m.name === item.name
          );
          if (exists) continue;
          await addMenu({
            parentId,
            name: item.name,
            type: SystemMenuTypeEnum.MENU,
            icon: item.icon,
            sort: item.sort,
            path: item.path,
            component: item.component,
            componentName: item.componentName,
            status: CommonStatusEnum.ENABLE,
            visible: true,
            keepAlive: true,
            alwaysShow: true,
            permission: ''
          });
          created.push(item.name);
        }
        if (created.length) {
          const after = await listMenu();
          const afterList = after.data || [];
          for (const item of EMBEDDED_MENUS) {
            const menu = afterList.find(m => m.component === item.component || m.name === item.name);
            if (menu && menu.id) {
              await this.assignMenuToCommonRoles(menu.id, item);
            }
          }
          this.$modal.msgSuccess(`已自动添加：${created.join('、')}，请重新登录后侧栏生效`);
          await this.getList();
        }
        sessionStorage.setItem(FLAG, '1');
      } catch (e) {
        console.warn('[ensureEmbeddedMenus]', e);
      }
    },
    async assignMenuToMyRoles(menuId) {
      const userId = this.$store.getters.userId;
      if (!userId) return { ok: [], fail: [] };
      const roleRes = await listUserRoles(userId);
      const roleIds = roleRes.data || [];
      return this.assignMenuToRoleIds(menuId, roleIds.map(id => ({ id, name: String(id) })));
    },
    /** 收集菜单祖先 id（侧栏需父级目录一并授权才可见） */
    collectAncestorMenuIds(menuId) {
      const flat = [];
      const walk = (nodes) => {
        (nodes || []).forEach((n) => {
          flat.push(n);
          if (n.children && n.children.length) walk(n.children);
        });
      };
      walk(this.menuList || []);
      const byId = {};
      flat.forEach((m) => { if (m && m.id != null) byId[m.id] = m; });
      const ancestors = [];
      let cur = byId[menuId] || byId[Number(menuId)];
      const guard = new Set();
      while (cur && cur.parentId != null && Number(cur.parentId) !== 0) {
        const pid = Number(cur.parentId);
        if (!pid || guard.has(pid)) break;
        guard.add(pid);
        ancestors.push(pid);
        cur = byId[pid];
      }
      return ancestors;
    },
    /** 将菜单追加到指定角色（已有则跳过；同时补齐父级目录） */
    async appendMenuToRole(roleId, menuId) {
      if (!roleId || !menuId) return;
      const menusRes = await listRoleMenus(roleId);
      const menuIds = (menusRes.data || []).map(Number);
      const need = [Number(menuId), ...this.collectAncestorMenuIds(menuId)]
        .filter((id) => id && !menuIds.includes(Number(id)));
      if (!need.length) return;
      await assignRoleMenu({
        roleId,
        menuIds: [...menuIds, ...need]
      });
    },
    /**
     * 并行授权并汇总失败角色
     * @returns {{ ok: string[], fail: string[] }}
     */
    async assignMenuToRoleIds(menuId, roles) {
      const list = (roles || []).filter(r => r && r.id != null);
      if (!list.length) return { ok: [], fail: [] };
      const settled = await Promise.allSettled(
        list.map(async (role) => {
          await this.appendMenuToRole(role.id, menuId);
          return role.name || role.code || String(role.id);
        })
      );
      const ok = [];
      const fail = [];
      settled.forEach((r, i) => {
        const label = list[i].name || list[i].code || String(list[i].id);
        if (r.status === 'fulfilled') ok.push(r.value || label);
        else fail.push(label);
      });
      return { ok, fail };
    },
    /** 新增菜单后同步给业务角色白名单；敏感 system 菜单跳过自动下发 */
    async assignMenuToCommonRoles(menuId, form, options = {}) {
      const quiet = !!(options && options.quiet);
      if (isSensitiveSystemMenu(form || this.form || {})) {
        if (!quiet) this.$modal.msgWarning('系统类菜单未自动授权，请到「角色管理」按需分配');
        return { ok: [], fail: [], skipped: true };
      }
      try {
        const res = await listSimpleRoles();
        const roles = (res.data || []).filter((role) => isAutoAssignRole(role));
        if (!roles.length) {
          // 白名单角色不存在时，降级为当前用户角色
          const fallback = await this.assignMenuToMyRoles(menuId);
          if (!quiet) this.reportAssignResult(fallback);
          return fallback;
        }
        const result = await this.assignMenuToRoleIds(menuId, roles);
        if (!quiet) this.reportAssignResult(result);
        return result;
      } catch (e) {
        console.warn('[assignMenuToCommonRoles]', e);
        const fallback = await this.assignMenuToMyRoles(menuId);
        if (!quiet) this.reportAssignResult(fallback);
        return fallback;
      }
    },
    reportAssignResult(result) {
      if (!result || result.skipped) return;
      const fail = result.fail || [];
      if (fail.length) {
        this.$modal.msgWarning(`部分角色授权失败：${fail.join('、')}，请到角色管理补授权`);
      }
    },
    /** 将已开启且显示的菜单批量同步给业务角色（解决历史菜单普通用户不可见） */
    async syncVisibleMenusToRoles() {
      try {
        await this.$modal.confirm('将把状态=开启且显示的非系统菜单同步授权给业务角色（科德-管理员 / common / 组成员 / 组长），是否继续？');
      } catch (e) {
        return;
      }
      this.loading = true;
      try {
        const response = await listMenu();
        const list = response.data || [];
        const targets = list.filter((m) => {
          if (!m || m.id == null) return false;
          if (isSensitiveSystemMenu(m)) return false;
          const enabled = Number(m.status) === Number(CommonStatusEnum.ENABLE) || m.status === CommonStatusEnum.ENABLE;
          const visible = m.visible !== false && m.visible !== 0 && m.visible !== '0';
          return enabled && visible;
        });
        let okCount = 0;
        let failCount = 0;
        for (const menu of targets) {
          const result = await this.assignMenuToCommonRoles(menu.id, menu, { quiet: true });
          if (result && result.skipped) continue;
          okCount += (result && result.ok && result.ok.length) || 0;
          failCount += (result && result.fail && result.fail.length) || 0;
        }
        this.$modal.msgSuccess(`已同步 ${targets.length} 个可见菜单；角色授权成功 ${okCount} 次` + (failCount ? `，失败 ${failCount} 次` : '') + '。普通用户请重新登录后查看侧栏');
      } catch (e) {
        console.warn('[syncVisibleMenusToRoles]', e);
        this.$modal.msgError('同步授权失败，请稍后重试');
      } finally {
        this.loading = false;
      }
    },
    /** 查询菜单列表 */
    getList() {
      this.loading = true;
      return listMenu(this.queryParams).then(response => {
        this.menuList = this.handleTree(response.data, "id");
        this.loading = false;
      }).catch(() => {
        this.loading = false;
      });
    },
    /** 转换菜单数据结构 */
    normalizer(node) {
      if (node.children && !node.children.length) {
        delete node.children;
      }
      return {
        id: node.id,
        label: node.name,
        children: node.children
      };
    },
    /** 查询菜单下拉树结构 */
    getTreeselect() {
      listMenu().then(response => {
        this.menuOptions = [];
        const menu = { id: 0, name: '主类目', children: [] };
        menu.children = this.handleTree(response.data, "id");
        this.menuOptions.push(menu);
      });
    },
    // 取消按钮
    cancel() {
      this.open = false;
      this.reset();
    },
    // 表单重置
    reset() {
      this.form = {
        id: undefined,
        parentId: 0,
        name: undefined,
        icon: undefined,
        type: SystemMenuTypeEnum.DIR,
        sort: undefined,
        status: CommonStatusEnum.ENABLE,
        visible: true,
        keepAlive: true,
        alwaysShow: true,
        openToCommonUsers: false,
      };
      this.resetForm("form");
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm");
      this.handleQuery();
    },
    /** 展开/折叠操作 */
    toggleExpandAll() {
      this.refreshTable = false;
      this.isExpandAll = !this.isExpandAll;
      this.$nextTick(() => {
        this.refreshTable = true;
      });
    },
    /** 新增按钮操作 */
    handleAdd(row) {
      this.reset();
      this.getTreeselect();
      if (row != null && row.id) {
        this.form.parentId = row.id;
      } else {
        this.form.parentId = 0;
      }
      this.open = true;
      this.title = "添加菜单";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      this.getTreeselect();
      getMenu(row.id).then(response => {
        this.form = Object.assign({ openToCommonUsers: false }, response.data || {});
        this.open = true;
        this.title = "修改菜单";
      });
    },
    /** 提交按钮 */
    submitForm: function() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          // 若权限类型为目录或者菜单时，进行 path 的校验，避免后续拼接出来的路由无法跳转
          if (this.form.type === SystemMenuTypeEnum.DIR
            || this.form.type === SystemMenuTypeEnum.MENU) {
            // 如果是外链，则不进行校验
            const path = this.form.path
            if (!isExternal(path)) {
              // 父权限为根节点，path 必须以 / 开头
              if (this.form.parentId === 0 && path.charAt(0) !== '/') {
                this.$modal.msgSuccess('前端必须以 / 开头')
                return
              } else if (this.form.parentId !== 0 && path.charAt(0) === '/') {
                this.$modal.msgSuccess('前端不能以 / 开头')
                return
              }
            }
          }

          // 提交；124：仅当勾选「开放普通用户」时才把该单页授权给业务角色
          if (this.form.id !== undefined) {
            updateMenu(this.form).then(async () => {
              const enabled = Number(this.form.status) === Number(CommonStatusEnum.ENABLE) || this.form.status === CommonStatusEnum.ENABLE;
              if (this.form.openToCommonUsers && enabled && this.form.visible !== false && this.form.visible !== 0) {
                await this.assignMenuToCommonRoles(this.form.id, this.form);
              }
              this.$modal.msgSuccess("修改成功" + (this.form.openToCommonUsers ? "，已开放给普通用户" : ""));
              this.open = false;
              this.getList();
            });
          } else {
            addMenu(this.form).then(async (response) => {
              const menuId = (response && (response.data || response.data === 0)) ? response.data : null;
              if (menuId && this.form.openToCommonUsers) {
                await this.assignMenuToCommonRoles(menuId, this.form);
                this.$modal.msgSuccess("新增成功，已开放该页给普通用户（重新登录后侧栏生效）");
              } else {
                this.$modal.msgSuccess("新增成功");
              }
              this.open = false;
              this.getList();
            });
          }
        }
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      this.$modal.confirm('是否确认删除名称为"' + row.name + '"的数据项?').then(function() {
          return delMenu(row.id);
        }).then(() => {
          this.getList();
          this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 批量删除操作 */
    async handleDeleteBatch() {
      await this.$modal.confirm('是否确认批量删除选中的菜单数据?')
      try {
        await delMenuList(this.checkedIds);
        this.checkedIds = [];
        await this.getList();
        this.$modal.msgSuccess("删除成功");
      } catch {}
    },
    /** 选择行数据 */
    handleRowCheckboxChange(records) {
      this.checkedIds = records.map((item) => item.id);
    }
  }
};
</script>
