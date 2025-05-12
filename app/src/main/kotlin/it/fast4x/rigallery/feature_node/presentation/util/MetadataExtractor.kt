package it.fast4x.rigallery.feature_node.presentation.util

import com.drew.imaging.ImageMetadataReader
import com.drew.metadata.Metadata
import com.drew.metadata.exif.ExifIFD0Directory
import com.drew.metadata.exif.ExifSubIFDDirectory
import java.io.File


fun MetadataExtractor(path: String) {
    if (path.isEmpty()) return
    val file = File(path)
    if (!file.exists()) return

    val metadata: Metadata? = try {
        ImageMetadataReader.readMetadata(file)
    } catch (e: Exception) {
        null
    }

    if (metadata == null) {
        println("MetadataExtractor: metadata is null")
        return
    }

    val exifSubIFDDirectories = metadata.getDirectoriesOfType<ExifSubIFDDirectory>(
        ExifSubIFDDirectory::class.java
    )
    val exifIFD0Directories = metadata.getFirstDirectoryOfType<ExifIFD0Directory>(
        ExifIFD0Directory::class.java
    )

    if (exifSubIFDDirectories != null) {
        for (directory in metadata.directories) {
            for (tag in directory.tags) {
                //if (tag.tagName.contains("lat"))
                    println("MetadataExtractor: directory: Exif SubIFD tag: ${tag.tagName} - ${tag}")
            }
        }
    }
    if (exifIFD0Directories != null) {
        for (directory in metadata.directories) {
            for (tag in directory.tags) {
                //if (tag.tagName.contains("lat"))
                    println("MetadataExtractor: directory: Exif IFD tag: ${tag.tagName} - ${tag}")
            }
        }
    }

//    for (directory in metadata.directories) {
//        for (tag in directory.tags) {
//            println("MetadataExtractor: directory: ${directory.name} tag: ${tag.tagName}")
//        }
//    }
}