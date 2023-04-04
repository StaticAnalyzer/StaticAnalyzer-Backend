package com.staticanalyzer.staticanalyzer.controller;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.staticanalyzer.staticanalyzer.entities.Project;
import com.staticanalyzer.staticanalyzer.entities.Result;

@RestController
public class ProjectController {
    class Task implements Runnable {
        private static ExecutorService jobCache = Executors.newCachedThreadPool();
        private Project project;

        public Task(Project project) {
            this.project = project;
        }

        public Project getProject() {
            return project;
        }

        public void setProject(Project project) {
            this.project = project;
        }

        @Override
        public void run() {
        }
    }

    @PostMapping("/project")
    public Result upload(@RequestBody int userId,
            @RequestBody MultipartFile sourceCode,
            @RequestBody String config) {
        return Result.success();
    }

    @GetMapping("/project/{id}")
    public Result queryStatus(@PathVariable int id) {
        return Result.success();
    }
}
