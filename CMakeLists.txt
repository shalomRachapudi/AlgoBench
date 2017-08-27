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
#           - .
#           - .
#           - .
#       - AlgoBench_GUI.jar
#   - backEnd
#       - AlgoBench_Backend
#   - AlgoBench.desktop

# ~/.local/share/applications
#create App directory


if(WIN32 OR WIN64)
	set(App_PATH "C:/AlgoBench_App")
else()
	set(App_PATH "$ENV{HOME}/AlgoBench_App")
endif()
message(${App_PATH})
INSTALL(DIRECTORY DESTINATION ${App_PATH})

# create the remaining directories
INSTALL(DIRECTORY DESTINATION ${App_PATH}/input)
INSTALL(DIRECTORY DESTINATION ${App_PATH}/output)
INSTALL(DIRECTORY DESTINATION ${App_PATH}/saved)
INSTALL(DIRECTORY DESTINATION ${App_PATH}/frontEnd)
INSTALL(DIRECTORY DESTINATION ${App_PATH}/images)
INSTALL(DIRECTORY DESTINATION ${App_PATH}/frontEnd/lib)
INSTALL(DIRECTORY DESTINATION ${App_PATH}/backEnd)
INSTALL(DIRECTORY DESTINATION ${App_PATH}/xml)
INSTALL(DIRECTORY DESTINATION ${App_PATH}/xml/xslt)


#install/copy jars into their respective directories
#lib jars
set(lib_jar_PATH "${CMAKE_CURRENT_SOURCE_DIR}/Frontend/lib")
set(frontEnd_lib_dest_PATH "${App_PATH}/frontEnd/lib")
INSTALL(FILES ${lib_jar_PATH}/absolutelayout/AbsoluteLayout.jar DESTINATION ${frontEnd_lib_dest_PATH})
INSTALL(FILES ${lib_jar_PATH}/beans-binding/beansbinding-1.2.1.jar DESTINATION ${frontEnd_lib_dest_PATH})
INSTALL(FILES ${lib_jar_PATH}/CopyLibs/org-netbeans-modules-java-j2seproject-copylibstask.jar DESTINATION ${frontEnd_lib_dest_PATH})
INSTALL(FILES ${lib_jar_PATH}/xchart-2.5.0.jar DESTINATION ${frontEnd_lib_dest_PATH})
INSTALL(FILES ${lib_jar_PATH}/avalon-framework-4.2.0.jar DESTINATION ${frontEnd_lib_dest_PATH})
INSTALL(FILES ${lib_jar_PATH}/batik-all-1.7.jar DESTINATION ${frontEnd_lib_dest_PATH})
INSTALL(FILES ${lib_jar_PATH}/commons-io-1.3.1.jar DESTINATION ${frontEnd_lib_dest_PATH})
INSTALL(FILES ${lib_jar_PATH}/commons-logging-1.0.4.jar DESTINATION ${frontEnd_lib_dest_PATH})
INSTALL(FILES ${lib_jar_PATH}/fop.jar DESTINATION ${frontEnd_lib_dest_PATH})
INSTALL(FILES ${lib_jar_PATH}/serializer-2.7.0.jar DESTINATION ${frontEnd_lib_dest_PATH})
INSTALL(FILES ${lib_jar_PATH}/xalan-2.7.0.jar DESTINATION ${frontEnd_lib_dest_PATH})
INSTALL(FILES ${lib_jar_PATH}/xercesImpl-2.7.1.jar DESTINATION ${frontEnd_lib_dest_PATH})
INSTALL(FILES ${lib_jar_PATH}/xml-apis-1.3.04.jar DESTINATION ${frontEnd_lib_dest_PATH})
INSTALL(FILES ${lib_jar_PATH}/xmlgraphics-commons-1.5.jar DESTINATION ${frontEnd_lib_dest_PATH})
INSTALL(FILES ${lib_jar_PATH}/xml-apis-ext-1.3.04.jar DESTINATION ${frontEnd_lib_dest_PATH})

# Copy AlgoBench_GUI.jar and AlgoBench_Backend 
# Path of AlgoBench_GUI => build/Frontend/src/
# Path of AlgoBench_Backend => build/Backend/
INSTALL(FILES ${CMAKE_CURRENT_SOURCE_DIR}/build/Frontend/src/AlgoBench_GUI.jar DESTINATION ${App_PATH}/frontEnd/)

if(WIN32 OR WIN64)
	INSTALL(PROGRAMS ${CMAKE_CURRENT_SOURCE_DIR}/build/Backend/Algobench_Backend.exe 
     	   DESTINATION ${App_PATH}/backEnd/
    	)

    	
    #install xslt file
    INSTALL(FILES ${CMAKE_CURRENT_SOURCE_DIR}/Frontend/src/xml/xslt/task.xsl
            DESTINATION ${App_PATH}/xml/xslt/
        )
        
    INSTALL(PROGRAMS ${CMAKE_CURRENT_SOURCE_DIR}/Frontend/AlgoBench.exe
     	   DESTINATION ${App_PATH}/
    	)
elseif(APPLE)
	INSTALL(PROGRAMS ${CMAKE_CURRENT_SOURCE_DIR}/build/Backend/Algobench_Backend 
    	    DESTINATION ${App_PATH}/backEnd/
    	)
    
    
    #install xslt file
    INSTALL(FILES ${CMAKE_CURRENT_SOURCE_DIR}/Frontend/src/xml/xslt/task.xsl
            DESTINATION ${App_PATH}/xml/xslt/
        )
        
	# install AlgoBench.command 
	INSTALL(PROGRAMS ${CMAKE_CURRENT_SOURCE_DIR}/Frontend/AlgoBench.command
    	    DESTINATION ${App_PATH}/
    	)
elseif(UNIX)
	INSTALL(PROGRAMS ${CMAKE_CURRENT_SOURCE_DIR}/build/Backend/Algobench_Backend 
    	    DESTINATION ${App_PATH}/backEnd/
    	)
    	
    #install xslt file
    INSTALL(FILES ${CMAKE_CURRENT_SOURCE_DIR}/Frontend/src/xml/xslt/task.xsl
            DESTINATION ${App_PATH}/xml/xslt/
        )
    
	# install AlgoBench.desktop 
	INSTALL(PROGRAMS ${CMAKE_CURRENT_SOURCE_DIR}/Frontend/AlgoBench.desktop
    	    DESTINATION "~/.local/share/applications"
    	)
endif()
