cmake_minimum_required(VERSION 3.16)
project(Lift-Pass-Pricing VERSION 1.0
        DESCRIPTION "Lift-Pass-Pricing refactoring kata."
        LANGUAGES CXX)
set(CMAKE_CXX_STANDARD 14)
set(CMAKE_BUILD_TYPE Debug)

enable_testing()

include(FetchContent)
FetchContent_Declare(googletest
        GIT_REPOSITORY https://github.com/google/googletest.git
        GIT_TAG        v1.14.0
        GIT_SHALLOW    1
)
FetchContent_MakeAvailable(googletest)
include(GoogleTest)

if(WIN32)
    # Paths on Windows after unpacking mysql-connector-c++-8.0.33-winx64-debug.zip in ./mysqlcppconn
    set(MYSQL_HOME_DIR "${CMAKE_CURRENT_SOURCE_DIR}/mysqlcppconn/mysql-connector-c++-8.0.33-winx64")
    set(MYSQL_INCLUDE_DIR "${MYSQL_HOME_DIR}/include/jdbc")
    set(MYSQL_LIBRARY_DIR "${MYSQL_HOME_DIR}/lib64/vs14")
else()
    set(MYSQL_HOME_DIR "/usr/local/mysql/connector-c++-8.0.29-debug")
    set(MYSQL_INCLUDE_DIR "${MYSQL_HOME_DIR}/include/jdbc")
    set(MYSQL_LIBRARY_DIR "${MYSQL_HOME_DIR}/lib64/debug")
    set(SYSTEM_LIB_DIR "/usr/lib/x86_64-linux-gnu")
    set(CMAKE_THREAD_LIBS_INIT "-lpthread")
endif()

add_library(mysql_static STATIC IMPORTED)
add_library(mysql STATIC IMPORTED)
add_library(crypto STATIC IMPORTED)
add_library(ssl STATIC IMPORTED)

if(WIN32)
    set_property(TARGET mysql_static PROPERTY
        IMPORTED_LOCATION "${MYSQL_LIBRARY_DIR}/debug/mysqlcppconn-static.lib")
    set_property(TARGET mysql PROPERTY
        IMPORTED_LOCATION "${MYSQL_LIBRARY_DIR}/debug/mysqlcppconn.lib")
    set_property(TARGET crypto PROPERTY
        IMPORTED_LOCATION "${MYSQL_LIBRARY_DIR}/libcrypto.lib")
    set_property(TARGET ssl PROPERTY
        IMPORTED_LOCATION "${MYSQL_LIBRARY_DIR}/libssl.lib")
else()
    # Paths to set when connector was installed manually, uses MYSQL_HOME_DIR
    # set_property(TARGET mysql_static PROPERTY
    #     IMPORTED_LOCATION ${MYSQL_LIBRARY_DIR}/libmysqlcppconn-static.a)
    # set_property(TARGET mysql PROPERTY
    #     IMPORTED_LOCATION ${MYSQL_LIBRARY_DIR}/libmysqlcppconn.so)

    # Paths on Ubuntu after "apt-get install libmysqlcppconn-dev", ignores MYSQL_HOME_DIR
    set_property(TARGET mysql_static PROPERTY
        IMPORTED_LOCATION ${SYSTEM_LIB_DIR}/libmysqlcppconn.a)
    set_property(TARGET mysql PROPERTY
        IMPORTED_LOCATION ${SYSTEM_LIB_DIR}/libmysqlcppconn.so)

    set_property(TARGET crypto PROPERTY
        IMPORTED_LOCATION ${SYSTEM_LIB_DIR}/libcrypto.so)
    set_property(TARGET ssl PROPERTY
        IMPORTED_LOCATION ${SYSTEM_LIB_DIR}/libssl.so)
endif()

include_directories("${PROJECT_SOURCE_DIR}" "${MYSQL_INCLUDE_DIR}")
link_directories("${MYSQL_LIBRARY_DIR}")

add_executable(lift-pass
               app.cpp Prices.cpp Prices.h httplib.h)
target_link_libraries(lift-pass PRIVATE ${CMAKE_THREAD_LIBS_INIT} mysql mysql_static crypto ssl)
if(WIN32)
    target_link_libraries(lift-pass PRIVATE wsock32 ws2_32)
endif()

add_executable(lift-pass-tests test/main.cpp
                               test/prices_gtest.cpp test/json.hpp
                               Prices.cpp Prices.h httplib.h)
target_include_directories(lift-pass-tests PRIVATE ${PROJECT_SOURCE_DIR}/test)
target_link_libraries(lift-pass-tests PRIVATE gtest ${CMAKE_THREAD_LIBS_INIT} mysql mysql_static crypto ssl)
if(WIN32)
    target_link_libraries(lift-pass-tests PRIVATE wsock32 ws2_32)
endif()

gtest_discover_tests(lift-pass-tests)
