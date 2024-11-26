package com.utils

class CommonSteps {
    static def createPathFolder(dslFactory, path) {
        String[] folders = path.split('/')
        String currentPath = ""
        folders.each { folder ->
            currentPath = (currentPath == "") ? folder : currentPath + '/' + folder
            dslFactory.folder(currentPath)
        }
    }
}