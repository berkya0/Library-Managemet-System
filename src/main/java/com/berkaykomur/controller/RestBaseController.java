package com.berkaykomur.controller;

import com.berkaykomur.model.RootEntity;

public class RestBaseController {
	
	public <T>RootEntity<T> ok(T payload) {
		return RootEntity.ok(payload);
	}
	public <T>RootEntity<T> error(String errorMessage) {
		return RootEntity.error(errorMessage);
	}
	// Void türü için özel ok() metodu (silme işlemleri için)
    public RootEntity<Void> ok() {
        return RootEntity.ok(null);
    }

}
