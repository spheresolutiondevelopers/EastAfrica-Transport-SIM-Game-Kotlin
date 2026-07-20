# Fix Compilation Errors in `NativeEngine.kt` and `Types.kt`

The goal is to resolve the compilation errors preventing the `:bridge` module from building.

## Proposed Changes

### [Component: Bridge](file:///C:/Users/lenovo/Projects/Transport%20simulation/TransportsimAndroid/bridge)

#### [MODIFY] [NativeEngine.kt](file:///C:/Users/lenovo/Projects/Transport%20simulation/TransportsimAndroid/bridge/src/main/java/com/transportsim/bridge/NativeEngine.kt)
- Move all `external` functions currently annotated with `@JvmStatic` from the class body into the `companion object`.
- This resolves the error `Only members in named objects and companion objects can be annotated with '@JvmStatic'`.
- Keeping `@JvmStatic` inside the `companion object` ensures these methods are exposed as static members of the `NativeEngine` class, which is likely required for JNI linkage.

#### [MODIFY] [Types.kt](file:///C:/Users/lenovo/Projects/Transport%20simulation/TransportsimAndroid/bridge/src/main/java/com/transportsim/bridge/Types.kt)
- Update `getActualTypeArguments()` to cast the `vararg` parameter `typeArguments` (which is inferred as `Array<out Type>`) to `Array<Type>`.
- This resolves the return type mismatch error.

## Verification Plan

### Automated Tests
- Run `./gradlew :bridge:assembleDebug` to verify that the module compiles without errors.

### Manual Verification
- N/A (Compilation fix only)
