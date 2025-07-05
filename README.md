Compatibility Layer for DLSS 2, FSR, and DSR in Minecraft 1.12.2
Bring modern GPU upscaling and frame generation to legacy Minecraft.
✨ Features
- 🧠 Real-time DLSS 2 integration via NVIDIA NGX SDK (Vulkan)
- 🔍 AMD FSR 2 support via FidelityFX SDK
- 🖼️ DSR-style resolution scaling fallback (OpenGL-only)
- 🌀 Simulated motion vectors for legacy rendering
- 🔄 OpenGL–Vulkan interop using shared memory and semaphores
- 🎛️ In-game Forge config GUI with sliders, toggles, and dropdowns
- 🎹 Hotkeys to toggle upscalers and HUD on the fly
- 📊 Performance logging with CSV + summary reports

🛠️ Requirements
| Component | Version | 
| Minecraft | 1.12.2 | 
| Forge | 14.23.x | 
| Java | 8 | 
| GPU | RTX (for DLSS), or any Vulkan-capable GPU (for FSR) | 
| OS | Windows or Linux (Vulkan + OpenGL interop required) | 



🚀 Installation
- Drop the .jar into your mods/ folder.
- Ensure Vulkan drivers and NGX/FSR SDKs are installed.

🎮 Controls
| Key | Action | 
| U | Cycle upscaler (DLSS → FSR → DSR → OFF) | 
| H | Toggle performance HUD | 



🖥️ Config GUI
Access via:
/dlssconfig


Adjust:
- DLSS quality mode
- Render scale
- Active upscaler

📊 Performance Logging
- Logs frame time, resolution, and upscaler to:
- compatlayer_metrics.csv
- compatlayer_summary.txt

🧠 Architecture
- Forge mod hooks into Minecraft’s framebuffer
- Motion vectors simulated from entity deltas
- OpenGL textures exported to Vulkan via GL_EXT_memory_object
- DLSS/FSR invoked via native JNI bridge
- Configurable via Forge GUI and runtime keybinds

🧪 Debug HUD
Displays:
- Active upscaler
- Frame time (ms)
- Resolution
Toggle with H.

🧰 Dev Notes
- Native code built via CMake (native/)
- Vulkan interop requires:
- VK_KHR_external_memory
- GL_EXT_semaphore
- DLSS requires NGX SDK and RTX GPU
- FSR 2 is open source and Vulkan-based


