// stdafx.h : include file for standard system include files,
// or project specific include files that are used frequently, but
// are changed infrequently
//

#ifndef STDAFX_H
#define STDAFX_H
#pragma once

// Standard C/C++
#include <iostream>
#include <fstream>
#include <ctime>
#include <string>
#include <sstream>
#include <math.h>
#include <vector>
#include <queue>
#include <unordered_map>
#include <map>
#include <stack>
#include <thread>
#include <algorithm>
#include <assert.h>
#include <random>
#include <signal.h>
#include <mutex>
#include <memory>
#include <atomic>
#include <sstream>
#include <functional>
#include <condition_variable>
#include <cstring>  
#include <set>
#include <cerrno>
#include <limits.h>     // since MS VC++ has issues with max() in <limits>

// contains pre-definitions for different platforms and utilities
#include "predef.h"

#if defined PREDEF_OS_WINDOWS
#include <tchar.h>
#include "targetver.h"
#include <Ws2tcpip.h>
#include <strsafe.h>
#include <WinSock2.h>
#elif defined PREDEF_OS_LINUX || defined PREDEF_OS_MACOSX
#include <stdio.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <arpa/inet.h>
#include <netinet/in.h>
#include <unistd.h>
#endif

#endif // STDAFX_H
