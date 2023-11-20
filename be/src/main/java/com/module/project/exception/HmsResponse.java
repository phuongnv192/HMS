package com.module.project.exception;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
public class HmsResponse<T> implements Serializable {
    @Serial
    private static final long serialVersionUID = 7851927773675224264L;

    private String code;
    private String message;
    private transient T data;
}
