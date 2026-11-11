package cn.iocoder.yudao.demo;

import cn.iocoder.yudao.module.infra.websocket.vo.MsgItem;
import com.alibaba.fastjson.JSON;
import io.swagger.v3.oas.models.security.SecurityScheme;

import java.awt.*;
import java.io.File;
import java.util.*;
import java.util.List;

public class TFont {

    public static void main(String[] args) throws Exception {
        // Font font = Font.createFont(Font.TRUETYPE_FONT, new File("/usr/share/fonts/dejavu/DejaVuSansCondensed-Oblique.ttf"));
        // List<Integer> arr = List.of(1, 2, 3);
        // for(Integer i : arr) {
        //     if(i == 2) continue;
        //     System.out.println("====>"+i);
        // }
        // test1();
        Map<String, ArrayDeque<Integer>> map = new HashMap<>();
        map.put("1", new ArrayDeque<>());
        TFont tFont = new TFont();
        tFont.handledMap(map);
        System.out.println("==============>check over! " + JSON.toJSONString(map));
    }

    public void handledMap(Map<String, ArrayDeque<Integer>> map) {
        map.get("1").push(1);
    }

    public static void test1() {
        Map<String, ArrayDeque<Integer>> map = new HashMap<>();
        ArrayDeque<Integer> queue = new ArrayDeque<>();
        queue.add(1);
        map.put("123", queue);
        map.getOrDefault("123", new ArrayDeque<>());
        // queue.add(2);
        // queue.add(3);
        Integer curr;
        Iterator<Integer> iterator = queue.iterator();
        while (!queue.isEmpty() && iterator.hasNext() && (curr = iterator.next()) != null) {
            if(!Objects.equals(curr, 1)) continue;
            iterator.remove();
            System.out.println(curr);
        }
        System.out.println(JSON.toJSONString(map.get("123")));
    }

}
