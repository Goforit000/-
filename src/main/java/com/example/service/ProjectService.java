package com.example.service;



import com.example.entity.Project;
import com.example.mapper.ProjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ProjectService {
     @Autowired
    private ProjectMapper projectMapper;

     //3用
    public List<Project> getProjectListThird() {
        return projectMapper.selectProjectsThird();
    }
        @Transactional
    public boolean addProject(Project project) {
        return projectMapper.insertProject(project) > 0;
    }


    public List<Project> getProjectList(int offset, int size, String name, String status) {
        return projectMapper.selectProjects(offset, size, name, status);
    }

    public int getTotalCount(String name, String status) {
        return projectMapper.selectTotalCount(name, status);
    }



@Transactional
    public boolean deleteProjectById(int projectId) {
        return projectMapper.deleteProjectById(projectId) > 0;
    }
@Transactional
    public boolean updateProject(Project project) {
        return projectMapper.updateProject(project) > 0;
    }


    public List<Project> searchByProjects(String projectName) {
        return projectMapper.searchProjects(projectName);
    }
}
