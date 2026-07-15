# Tasks

- [x] Fix Missing Includes
    - [x] Add `<jni.h>` to `Renderer.h` (Already present)
    - [x] Add `<android/native_window_jni.h>` to `transportsim_jni.cpp` (Already present)
- [x] Provide Stub Headers for Incomplete Types
    - [x] Define `PacejkaModel` in `Pacejka.h`
    - [x] Define `AckermannSolver` in `Ackermann.h`
    - [x] Define `SuspensionModel` in `Suspension.h`
    - [x] Define `VBOArena` in `VBOArena.h`
    - [x] Create `ShaderManager.h` and `ShaderManager.cpp`
    - [x] Create `TextureManager.h` and `TextureManager.cpp`
    - [x] Create `GLTFLoader.h` and `GLTFLoader.cpp`
    - [x] Define other missing physics/terrain stubs
- [/] Verification
    - [ ] Run build for `:native`
