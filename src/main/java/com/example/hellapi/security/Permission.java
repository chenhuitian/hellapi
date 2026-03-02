package com.example.hellapi.security;

/**
 * API permissions for role-based access control.
 * SYSTEM_ADMIN bypasses all checks; other roles are checked against these authorities.
 */
public enum Permission {

	PRODUCT_READ,
	PRODUCT_CREATE,
	PRODUCT_UPDATE,
	PRODUCT_DELETE,
	PRODUCT_LIST_DELETED,
	PRODUCT_RESTORE,
	ROLE_MANAGE
}
