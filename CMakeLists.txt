cmake_minimum_required(VERSION 3.10)
project(CompatLayerNative)

# Set C++ standard
set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

# Output directory
set(CMAKE_LIBRARY_OUTPUT_DIRECTORY ${CMAKE_BINARY_DIR}/bin)

# Source files
set(SOURCES
    dlss_bridge.cpp
    fsr_bridge.cpp
    dsr_bridge.cpp
    interop_bridge.cpp
    interop_image.cpp
)

# Include headers
include_directories(include)

# Find JNI
find_package(Java REQUIRED)
find_package(JNI REQUIRED)
include_directories(${JNI_INCLUDE_DIRS})

# Vulkan SDK
if(NOT DEFINED ENV{VULKAN_SDK})
    message(FATAL_ERROR "VULKAN_SDK environment variable not set")
endif()

set(VULKAN_INCLUDE_DIR "$ENV{VULKAN_SDK}/Include")
set(VULKAN_LIB_DIR "$ENV{VULKAN_SDK}/Lib")
include_directories(${VULKAN_INCLUDE_DIR})
link_directories(${VULKAN_LIB_DIR})

# Platform-specific OpenGL and linking
if(WIN32)
    set(PLATFORM_LIBS opengl32)
    add_definitions(-D_CRT_SECURE_NO_WARNINGS)
else()
    find_package(OpenGL REQUIRED)
    include_directories(${OPENGL_INCLUDE_DIR})
    set(PLATFORM_LIBS ${OPENGL_gl_LIBRARY} dl pthread)
endif()

# Build shared library
add_library(dlss_bridge SHARED ${SOURCES})

# Link Vulkan and OpenGL
target_link_libraries(dlss_bridge
    ${PLATFORM_LIBS}
    vulkan-1
)