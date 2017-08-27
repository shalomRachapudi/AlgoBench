cmake_minimum_required (VERSION 2.8.12 FATAL_ERROR)
project(AlgoBench)

find_package(Java REQUIRED)
include(UseJava)

# Get properties' files
set(config_PATH "inf2b/algobench/config")
set(AlgoBench_CONFIG ${config_PATH}/config.properties 
				 ${config_PATH}/default.properties
				 ${config_PATH}/settings.xml
		)

# HTML/CSS files
set(html_PATH "inf2b/algobench/html")
file(GLOB inHtmlFiles RELATIVE "${CMAKE_CURRENT_SOURCE_DIR}" 
                 "${html_PATH}/*.html"
                 "${html_PATH}/*.css"
                 "${html_PATH}/*.gif"
                 "${html_PATH}/img/*.png"
        )

#get visual media
set(visualMedia_PATH "inf2b/algobench/images")
file(GLOB inVisualFiles RELATIVE "${CMAKE_CURRENT_SOURCE_DIR}"
                 "${visualMedia_PATH}/*.png"
                 "${visualMedia_PATH}/*.jpg"
                 "${visualMedia_PATH}/algo_icons/*.png"
        )

# set java source files. These are grouped into their respective packages
set(main_PATH "inf2b/algobench/main/")
set(AlgoBench_MAIN ${main_PATH}/AlgoBench.java)

set(model_PATH "inf2b/algobench/model")
set(AlgoBench_MODEL 
                 ${model_PATH}/ClientPipe.java
				 ${model_PATH}/MyChart.java 
				 ${model_PATH}/MyChartBuilder.java
				 ${model_PATH}/Plotter.java
				 ${model_PATH}/Task.java
				 ${model_PATH}/TaskMaster.java
		)

set(ui_PATH "inf2b/algobench/ui")
set(AlgoBench_UI ${ui_PATH}/CompareChartPanel.form
				 ${ui_PATH}/CompareChartPanel.java
				 ${ui_PATH}/CompareDialog.form
				 ${ui_PATH}/CompareDialog.java
				 ${ui_PATH}/Help.form
				 ${ui_PATH}/Help.java
				 ${ui_PATH}/MainWindow.form
				 ${ui_PATH}/MainWindow.java
				 ${ui_PATH}/NewTaskDialog.form
				 ${ui_PATH}/NewTaskDialog.java
				 ${ui_PATH}/ResultsChartPanel.form
				 ${ui_PATH}/ResultsChartPanel.java
				 ${ui_PATH}/ResultsTablePanel.form
				 ${ui_PATH}/ResultsTablePanel.java
				 ${ui_PATH}/SimpleSwingBrowser.java
				 ${ui_PATH}/TaskOverviewPanel.form
				 ${ui_PATH}/TaskOverviewPanel.java
				 ${ui_PATH}/EditTaskDialog.form
				 ${ui_PATH}/EditTaskDialog.java
				 ${ui_PATH}/components/GraphTaskSubPanel.form
				 ${ui_PATH}/components/GraphTaskSubPanel.java
				 ${ui_PATH}/components/HashTaskSubPanel.form
				 ${ui_PATH}/components/HashTaskSubPanel.java
				 ${ui_PATH}/components/SearchTaskSubPanel.form
				 ${ui_PATH}/components/SearchTaskSubPanel.java
				 ${ui_PATH}/components/SortTaskSubPanel.form
				 ${ui_PATH}/components/SortTaskSubPanel.java
		)

set(util_PATH "inf2b/algobench/util")
set(AlgoBench_UTIL 
                 ${util_PATH}/CheckBoxListRenderer.java
				 ${util_PATH}/CheckboxTableRenderer.java
 				 ${util_PATH}/CompareDetailTableRenderer.java
				 ${util_PATH}/InputGenerator.java
				 ${util_PATH}/ITaskCompleteListener.java
				 ${util_PATH}/ITaskCompleteNotifier.java
				 ${util_PATH}/ITaskSubPanel.java
				 ${util_PATH}/ITreeSubPanel.java
				 ${util_PATH}/ListRenderer.java
				 ${util_PATH}/SmartScroller.java
				 ${util_PATH}/Util.java
		)

# the following jars are needed to create/render java objects
#find xchart Jar lib
find_jar(xchart_JAR
			"xchart-2.5.0"
			PATHS "${CMAKE_CURRENT_SOURCE_DIR}/../lib/"
		)

#find valon-framework Jar lib
find_jar(avalon_JAR
			"avalon-framework-4.2.0"
			PATHS "${CMAKE_CURRENT_SOURCE_DIR}/../lib/"
		)

#find batik Jar lib
find_jar(batik_JAR
			"batik-all-1.7"
			PATHS "${CMAKE_CURRENT_SOURCE_DIR}/../lib/"
		)

#find commons-io Jar lib
find_jar(commons_io_JAR
			"commons-io-1.3.1"
			PATHS "${CMAKE_CURRENT_SOURCE_DIR}/../lib/"
		)

#find commons-logging Jar lib
find_jar(commons_log_JAR
			"commons-logging-1.0.4"
			PATHS "${CMAKE_CURRENT_SOURCE_DIR}/../lib/"
		)

#find fop Jar lib -- needed for pdf generation
find_jar(fop_JAR
			"fop"
			PATHS "${CMAKE_CURRENT_SOURCE_DIR}/../lib/"
		)

#find serializer Jar lib
find_jar(serializer_JAR
			"serializer-2.7.0"
			PATHS "${CMAKE_CURRENT_SOURCE_DIR}/../lib/"
		)

#find xalan Jar lib
find_jar(xalan_JAR
			"xalan-2.7.0"
			PATHS "${CMAKE_CURRENT_SOURCE_DIR}/../lib/"
		)
#find xerces Jar lib
find_jar(xerces_JAR
			"xercesImpl-2.7.1"
			PATHS "${CMAKE_CURRENT_SOURCE_DIR}/../lib/"
		)

#find xml-apis-1.3.04
find_jar(xml_JAR
			"xml-apis-1.3.04"
			PATHS "${CMAKE_CURRENT_SOURCE_DIR}/../lib/"
		)

#find xml-apis-ext
find_jar(xml_ext_JAR
			"xml-apis-ext-1.3.04"
			PATHS "${CMAKE_CURRENT_SOURCE_DIR}/../lib/"
		)

#find xmlgraphics
find_jar(xml_graphics_JAR
			"xmlgraphics-commons-1.5"
			PATHS "${CMAKE_CURRENT_SOURCE_DIR}/../lib/"
		)
#find AbsoluteLayout Jar lib
find_jar(absoluteLayout_JAR
			"AbsoluteLayout"
			PATHS "${CMAKE_CURRENT_SOURCE_DIR}/../lib/absolutelayout/"
		)

#find beans-binding Jar lib
find_jar(beansBinding_JAR
			"beansbinding-1.2.1"
			PATHS "${CMAKE_CURRENT_SOURCE_DIR}/../lib/beans-binding/"
		)

# since this project's developed using netbeans, we need the following JAR file
find_jar(netBeansModule_JAR
			"org-netbeans-modules-java-j2seproject-copylibstask"
			PATHS "${CMAKE_CURRENT_SOURCE_DIR}/../lib/CopyLibs/"
		)

#check if found 
message(${xchart_JAR})
message(${absoluteLayout_JAR})
message(${beansBinding_JAR})
message(${netBeansModule_JAR})
message(${xml_graphics_JAR})

# Create AlgoBench_GUI jar file
add_jar(AlgoBench_GUI 
			${AlgoBench_MAIN}
			${AlgoBench_UTIL} 
			${AlgoBench_MODEL} 
			${AlgoBench_UI} 
			${AlgoBench_CONFIG}
			${inHtmlFiles}
			${inVisualFiles}
        INCLUDE_JARS
			${xchart_JAR}
			${absoluteLayout_JAR}
			${beansBinding_JAR}
			${netBeansModule_JAR}
			${avalon_JAR}
			${batik_JAR}
			${commons_io_JAR}
			${commons_log_JAR}
			${fop_JAR}
			${serializer_JAR}
			${xalan_JAR}
			${xerces_JAR}
			${xml_JAR}
			${xml_ext_JAR}
			${xml_graphics_JAR}

		ENTRY_POINT
			inf2b/algobench/main/AlgoBench
        MANIFEST
            MANIFEST.MF
        )
        
# get props of the generated .jar file (i.e., AlgoBench_GUI.jar)
get_target_property(_jarFilePath AlgoBench_GUI JAR_FILE)
get_target_property(_classDirPath AlgoBench_GUI CLASSDIR)

message(STATUS "Jar file ${_jarFilePath}")
message(STATUS "Class compiled to ${_classDirPath}")

#installation of files
#icon is required for AlgoBench.desktop
INSTALL(FILES ${visualMedia_PATH}/icon.png DESTINATION /tmp/)
