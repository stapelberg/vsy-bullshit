#------------------------------------------------------------------------------
# VSY BBingo QT Client Build System

#------------------------------------------------------------------------------
# Main Build Configuration File

#------------------------------------------------------------------------------
# Include some CMake functions
include(CheckLibraryExists)
include(CheckIncludeFile)
include(CheckIncludeFileCXX)
include(CheckCCompilerFlag)
include(CheckCSourceCompiles)

#------------------------------------------------------------------------------
project( VSYBBingo ) 

cmake_minimum_required(VERSION 2.6)

set(EXECUTABLE_OUTPUT_PATH ${CMAKE_SOURCE_DIR}/Bin )
set(Bingo_EXECUTABLE BBingo_VSY )

#------------------------------------------------------------------------------
# Sources
file(GLOB_RECURSE VSYBBingo_SRCS ./src/*.cpp)

#------------------------------------------------------------------------------
# Headers / Meta Object Compiler headers
file(GLOB VSYBBingo_Common_Headers ./include/*.h )

set( VSYBBingo_MOC_HDRS
 # include/BingoMainWindow.h
  ${VSYBBingo_Common_Headers}
)

include_directories ( 
	include/
	#QJson
	qjson/
)

#------------------------------------------------------------------------------
# UI Files
file(GLOB VSYBBingo_UIS ./ui/*.ui )
  
#------------------------------------------------------------------------------
# Additional settings and resources
SET( VSYBBingo_RCS
   ./resources/bingo.qrc
)
  
# enable warnings
#add_definitions( -Wall  )
  
# by default only QtCore and QtGui modules are enabled
# so enable additonal libs  
find_package( Qt4 REQUIRED)
set( QT_USE_QTNETWORK TRUE )
#------------------------------------------------------------------------------
# add some useful macros and variables
# (QT_USE_FILE is a variable defined by FIND_PACKAGE( Qt4 ) that contains a path to CMake script)
include( ${QT_USE_FILE} )
  
QT4_ADD_RESOURCES( VSYBBingo_RC_SRC ${VSYBBingo_RCS} )

# this will run uic on .ui files:
QT4_WRAP_UI( VSYBBingo_UI_HDRS ${VSYBBingo_UIS} )

# and finally this will run moc:
QT4_WRAP_CPP( VSYBBingo_MOC_SRCS ${VSYBBingo_MOC_HDRS} )
  
# we need this to be able to include headers produced by uic in our code
# (CMAKE_BINARY_DIR holds a path to the build directory, while INCLUDE_DIRECTORIES() works just like INCLUDEPATH from qmake)
include_directories( ${CMAKE_BINARY_DIR} )

if(MSVC)
SET ( RESOURCE_FILES
  "./resources/QTClient.rc"
) 
endif()

#-----------------------------------------------------------------------------
# QJson
set(qjson_MOC_HDRS
  qjson/parserrunnable.h
  qjson/serializerrunnable.h
)

qt4_wrap_cpp(qjson_MOC_SRCS ${qjson_MOC_HDRS})
file(GLOB qjson_SRCS qjson/*.cpp )
file(GLOB qjson_HDRS qjson/*.h )
set(qjson_SRCS ${qjson_SRCS} qjson/json_parser.cc)


# MSVC Project Source Grouping
SOURCE_GROUP("Source Files\\QJSON\\" FILES  ${qjson_SRCS})
SOURCE_GROUP("Header Files\\QJSON\\" FILES  ${qjson_HDRS})
SOURCE_GROUP("GeneratedFiles\\" FILES 
	${VSYBBingo_MOC_SRCS} 
	${qjson_MOC_SRCS} 
	${VSYBBingo_RC_SRC} 
	
) 
SOURCE_GROUP("Resources\\" FILES ${RESOURCE_FILES})
SOURCE_GROUP("UI\\" FILES ${VSYBBingo_UIS} )
#SOURCE_GROUP("Translations\\" FILES ${VSYBBingo_Translations} )

#------------------------------------------------------------------------------
# Executable File and Linker directives
add_executable( 
	${Bingo_EXECUTABLE} 
	WIN32 MACOSX_BUNDLE 
	${VSYBBingo_MOC_SRCS}
	${qjson_HDRS}
	${qjson_MOC_SRCS}
	${qjson_SRCS}
	${VSYBBingo_SRCS} 	
	${VSYBBingo_RC_SRC} 
	${VSYBBingo_UI_HDRS} 
	${VSYBBingo_Common_Headers}
	${RESOURCE_FILES}

)

target_link_libraries( 
	${Bingo_EXECUTABLE}
	${QT_LIBRARIES} 
	${QT_QTNETWORK_LIBRARIES}
)