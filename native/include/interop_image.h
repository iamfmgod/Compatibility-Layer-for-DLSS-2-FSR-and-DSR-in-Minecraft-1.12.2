#ifndef INTEROP_IMAGE_H
#define INTEROP_IMAGE_H

#include <vulkan/vulkan.h>

VkImage createSharedOutputImage(VkDevice device, VkPhysicalDevice physicalDevice, int width, int height, VkFormat format);
void destroySharedOutputImage(VkDevice device);

#endif