cmake_minimum_required(VERSION 2.8.12 FATAL_ERROR)

add_subdirectory(Frontend)
add_subdirectory(Backend)

# Group relevant files and store them in App directory
# Following is the structure
# App
#   - input
#   - output
#   - saved 
#   - frontEnd
#       - lib 
#           - AbsoluteLayout
#           - beansbinding
#           - xchart
#       - AlgoBench_GUI.jar
#   - backEnd
#       - AlgoBench_Backend
#   - AlgoBench.desktop

# ~/.local/share/applications
#create App directory

set(App_PATH "$ENV{HOME}/AlgoBench_App")
INSTALL(DIRECTORY DESTINATION ${App_PATH})

# create the remaining directories
INSTALL(DIRECTORY DESTINATION ${App_PATH}/input)
INSTALL(DIRECTORY DESTINATION ${App_PATH}/output)
INSTALL(DIRECTORY DESTINATION ${App_PATH}/saved)
INSTALL(DIRECTORY DESTINATION ${App_PATH}/frontEnd)
INSTALL(DIRECTORY DESTINATION ${App_PATH}/frontEnd/lib)
INSTALL(DIRECTORY DESTINATION ${App_PATH}/backEnd)

#install/copy jars into their respective directories
#lib jars
set(lib_jar_PATH "${CMAKE_CURRENT_SOURCE_DIR}/Frontend/lib")
set(frontEnd_lib_dest_PATH "${App_PATH}/frontEnd/lib")
INSTALL(FILES ${lib_jar_PATH}/absolutelayout/AbsoluteLayout.jar DESTINATION ${frontEnd_lib_dest_PATH})
INSTALL(FILES ${lib_jar_PATH}/beans-binding/beansbinding-1.2.1.jar DESTINATION ${frontEnd_lib_dest_PATH})
INSTALL(FILES ${lib_jar_PATH}/CopyLibs/org-netbeans-modules-java-j2seproject-copylibstask.jar DESTINATION ${frontEnd_lib_dest_PATH})
INSTALL(FILES ${lib_jar_PATH}/xchart-2.5.0.jar DESTINATION ${frontEnd_lib_dest_PATH})

# Copy AlgoBench_GUI.jar and AlgoBench_Backend 
# Path of AlgoBench_GUI => build/Frontend/src/
# Path of AlgoBench_Backend => build/Backend/
INSTALL(FILES ${CMAKE_CURRENT_SOURCE_DIR}/build/Frontend/src/AlgoBench_GUI.jar DESTINATION ${App_PATH}/frontEnd/)
INSTALL(PROGRAMS ${CMAKE_CURRENT_SOURCE_DIR}/build/Backend/Algobench_Backend 
        DESTINATION ${App_PATH}/backEnd/
    )

# install AlgoBench.desktop 
INSTALL(PROGRAMS ${CMAKE_CURRENT_SOURCE_DIR}/Frontend/AlgoBench.desktop
        DESTINATION "~/.local/share/applications"
    )
