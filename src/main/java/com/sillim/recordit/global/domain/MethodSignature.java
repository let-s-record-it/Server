package com.sillim.recordit.global.domain;

import org.springframework.web.method.HandlerMethod;

public record MethodSignature(String className, String methodName) {

	public static MethodSignature extract(HandlerMethod handlerMethod) {
		String className = handlerMethod.getBeanType().getSimpleName();
		String methodName = handlerMethod.getMethod().getName();
		return new MethodSignature(className, methodName);
	}
}
