import com.syssim.utils.Constants
import com.syssim.utils.CommonSteps

void createAllDirectories(String path) {
    String[] directories = path.split('/')
    String currentPath = ""

    directories.each { directory ->
        currentPath = (currentPath == "") ? directory : currentPath + '/' + directory
        folder(currentPath) {}
    }
}

// Create the folders recursively
def FOLDER_NAME = "pipelines"
createAllDirectories(FOLDER_NAME)