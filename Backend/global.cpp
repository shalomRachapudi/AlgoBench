#include "stdafx.h"
#include "global.h"

namespace inf2b
{
    std::vector<std::string> progressReport;
    std::mutex myMutex;
    Semaphore progressSemaphore { };
    Semaphore socketReadySemaphore { };
    std::string ALGO_INPUT_FILENAME;
    size_t MemoryUsage = 0;
    std::map<std::string, size_t> Command;
}
