package com.xchinfo.erp.bpm.engine;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 参与者选择实现类
 *
 * @author roman.li
 * @date 2019/3/21
 * @update
 */
@Component
public class ActorAssignmentService {
    public List<String> assignByGroup(String groupId) {
        List<String> actors = new ArrayList<>();
        actors.add("1");

        return actors;
    }

    public List<String> assginByRole(String roleId) {
        List<String> actors = new ArrayList<>();
        actors.add("1");

        return actors;
    }

    public String assginByUser(String userId) {
        return "1";
    }
}
