package com.codemate.runtime.task;

@FunctionalInterface
public interface TaskRunner {
    String run(String prompt) throws Exception;
}
