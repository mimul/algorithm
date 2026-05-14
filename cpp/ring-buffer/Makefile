CXX      = g++
CXXFLAGS = -Wall -O2 -std=c++17

TARGET   = ring_buffer
SRC      = ring_buffer.cpp

# brew install googletest 후 설치되는 경로 (Apple Silicon: /opt/homebrew)
BREW_PREFIX  ?= /usr/local
GTEST_INC     = -I$(BREW_PREFIX)/include
GTEST_LIB     = -L$(BREW_PREFIX)/lib -lgtest -lgtest_main -lpthread

TEST_TARGET = ring_buffer_test
TEST_SRC    = ring_buffer_test.cpp

.PHONY: all test clean

all: $(TARGET)

$(TARGET): $(SRC) ring_buffer.h
	$(CXX) $(CXXFLAGS) -O3 -march=native -o $@ $(SRC)

$(TEST_TARGET): $(TEST_SRC) ring_buffer.h
	$(CXX) $(CXXFLAGS) $(GTEST_INC) -o $@ $(TEST_SRC) $(GTEST_LIB)

test: $(TEST_TARGET)
	./$(TEST_TARGET)

clean:
	rm -f $(TARGET) $(TEST_TARGET)
