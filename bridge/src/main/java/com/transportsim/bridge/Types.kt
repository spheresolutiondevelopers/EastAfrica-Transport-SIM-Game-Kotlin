package com.transportsim.bridge

import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * Helper to create parameterized types for Moshi adapters.
 */
object Types {
    fun newParameterizedType(raw: Class<*>, vararg typeArguments: Type): Type {
        return object : ParameterizedType {
            override fun getRawType(): Type = raw
            override fun getOwnerType(): Type? = null
            override fun getActualTypeArguments(): Array<Type> = typeArguments
            override fun toString(): String = "${raw.name}<${typeArguments.joinToString(",")}>"
        }
    }
}