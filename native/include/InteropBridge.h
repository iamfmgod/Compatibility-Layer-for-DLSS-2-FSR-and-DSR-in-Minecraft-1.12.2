#include "InteropBridge.h"
#include <vulkan/vulkan.h>
#include <stdexcept>
#include <iostream>

static VkImage outputImage = VK_NULL_HANDLE;
static VkDeviceMemory outputMemory = VK_NULL_HANDLE;

VkImage createSharedOutputImage(VkDevice device, VkPhysicalDevice physicalDevice, int width, int height, VkFormat format) {
    if (outputImage != VK_NULL_HANDLE) return outputImage;

    VkExternalMemoryImageCreateInfo extInfo = {
        .sType = VK_STRUCTURE_TYPE_EXTERNAL_MEMORY_IMAGE_CREATE_INFO,
#ifdef _WIN32
        .handleTypes = VK_EXTERNAL_MEMORY_HANDLE_TYPE_OPAQUE_WIN32
#else
        .handleTypes = VK_EXTERNAL_MEMORY_HANDLE_TYPE_OPAQUE_FD_BIT
#endif
    };

    VkImageCreateInfo imageInfo = {
        .sType = VK_STRUCTURE_TYPE_IMAGE_CREATE_INFO,
        .pNext = &extInfo,
        .imageType = VK_IMAGE_TYPE_2D,
        .format = format,
        .extent = { (uint32_t)width, (uint32_t)height, 1 },
        .mipLevels = 1,
        .arrayLayers = 1,
        .samples = VK_SAMPLE_COUNT_1_BIT,
        .tiling = VK_IMAGE_TILING_OPTIMAL,
        .usage = VK_IMAGE_USAGE_STORAGE_BIT | VK_IMAGE_USAGE_SAMPLED_BIT,
        .sharingMode = VK_SHARING_MODE_EXCLUSIVE,
        .initialLayout = VK_IMAGE_LAYOUT_UNDEFINED
    };

    if (vkCreateImage(device, &imageInfo, nullptr, &outputImage) != VK_SUCCESS)
        throw std::runtime_error("Failed to create Vulkan output image");

    VkMemoryRequirements memReqs;
    vkGetImageMemoryRequirements(device, outputImage, &memReqs);

    uint32_t memoryTypeIndex = findMemoryTypeIndex(physicalDevice, memReqs.memoryTypeBits, VK_MEMORY_PROPERTY_DEVICE_LOCAL_BIT);

#ifdef _WIN32
    VkExportMemoryWin32HandleInfoKHR exportInfo = {
        .sType = VK_STRUCTURE_TYPE_EXPORT_MEMORY_WIN32_HANDLE_INFO_KHR,
        .handleTypes = VK_EXTERNAL_MEMORY_HANDLE_TYPE_OPAQUE_WIN32
    };
    VkExportMemoryAllocateInfo exportAlloc = {
        .sType = VK_STRUCTURE_TYPE_EXPORT_MEMORY_ALLOCATE_INFO,
        .pNext = &exportInfo,
        .handleTypes = VK_EXTERNAL_MEMORY_HANDLE_TYPE_OPAQUE_WIN32
    };
#else
    VkExportMemoryAllocateInfo exportAlloc = {
        .sType = VK_STRUCTURE_TYPE_EXPORT_MEMORY_ALLOCATE_INFO,
        .handleTypes = VK_EXTERNAL_MEMORY_HANDLE_TYPE_OPAQUE_FD_BIT
    };
#endif

    VkMemoryAllocateInfo allocInfo = {
        .sType = VK_STRUCTURE_TYPE_MEMORY_ALLOCATE_INFO,
        .pNext = &exportAlloc,
        .allocationSize = memReqs.size,
        .memoryTypeIndex = memoryTypeIndex
    };

    if (vkAllocateMemory(device, &allocInfo, nullptr, &outputMemory) != VK_SUCCESS)
        throw std::runtime_error("Failed to allocate Vulkan output memory");

    if (vkBindImageMemory(device, outputImage, outputMemory, 0) != VK_SUCCESS)
        throw std::runtime_error("Failed to bind Vulkan output memory");

    std::cout << "[InteropImage] Created shared Vulkan output image." << std::endl;
    return outputImage;
}

void destroySharedOutputImage(VkDevice device) {
    if (outputImage != VK_NULL_HANDLE) {
        vkDestroyImage(device, outputImage, nullptr);
        outputImage = VK_NULL_HANDLE;
    }
    if (outputMemory != VK_NULL_HANDLE) {
        vkFreeMemory(device, outputMemory, nullptr);
        outputMemory = VK_NULL_HANDLE;
    }
    std::cout << "[InteropImage] Destroyed Vulkan output image and memory." << std::endl;
}