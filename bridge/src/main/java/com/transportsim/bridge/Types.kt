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
            @Suppress("UNCHECKED_CAST")
            override fun getActualTypeArguments(): Array<Type> = typeArguments as Array<Type>
            override fun toString(): String = "${raw.name}<${typeArguments.joinToString(",")}>"
        }
    }
}