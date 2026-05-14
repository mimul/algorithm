---
name: Algorithm Project Coding Style Guide
description: C++/Java 알고리즘 프로젝트에서 코드 생성·수정 시 적용할 Domain Clarity, Change Safety, Explicit Intent 중심의 코딩 원칙과 언어별 실천 가이드
---

# Algorithm Project Coding Style Guide

이 저장소는 알고리즘 학습, 구현 비교, 문제 해결 능력 향상을 목적으로 한다.  
LLM(AI)을 활용해 코드를 생성·수정할 때 **언어별 스타일 가이드만으로는 부족**하다는 점을 고려하여, **Domain Clarity**, **Change Safety**, **Explicit Intent**를 최우선으로 하는 원칙을 적용한다.

## Core Philosophy (핵심 철학)

코드의 존재 목적은 **도메인 지식(알고리즘의 본질과 의도)을 안전하게 보존하고 발전**시키는 것이다.  
알고리즘 코드의 본질은 문법 기교나 최적화 트릭이 아니라, **문제 해결 의도와 알고리즘 동작을 명확하게 인코딩**하여 미래의 자신과 다른 사람이(그리고 AI가) 쉽게 이해하고 수정·확장·비교할 수 있게 하는 데 있다.

**최우선 고려 대상**:
- Domain Clarity (알고리즘 의도와 본질의 명확성)
- Change Safety (안전한 수정·리팩토링)
- Explicit Intent (명시적 의도 표현)
- Evolutionary Design (진화 가능한 코드)
- Cognitive Load Reduction (인지 부하 감소)

**최우선으로 고려하지 말아야 할 것**:
- Clever abstractions (과도하게 영리한 추상화)
- Minimal lines of code (라인 수 최소화)
- Framework purity (프레임워크 규칙을 도메인보다 우선하는 설계)
- Premature reuse (아직 필요하지 않은 시점의 조급한 재사용)
- Premature optimization (측정 없는 성능 최적화)
- Theoretical elegance (실용성보다 미학적 우아함)

### Core Philosophy 체크리스트
- [ ] 알고리즘의 본질(의도, 트레이드오프, 복잡도)이 코드와 주석에 명확하게 드러나는가?
- [ ] Domain Clarity, Explicit Intent, Change Safety를 최우선 가치로 삼았는가?
- [ ] 과도한 기교, 조기 최적화, 불필요한 추상화를 피했는가?

---

## 1. Domain First (알고리즘 도메인 우선)

코드 구조와 이름은 기술적 세부사항(언어 문법)보다 문제 유형(정렬, 그래프, DP, 트리 등)과 **알고리즘 본질**을 따라야 한다.  
알고리즘의 의도, 시간/공간 복잡도, 트레이드오프를 최우선으로 표현한다.

### Domain First 체크리스트
- [ ] 파일명, 클래스명, 함수명이 알고리즘의 본질과 문제를 명확히 드러내는가? (e.g. `DetectCycleFloydWarshall`)
- [ ] 문제 번호(LeetCode 등), 핵심 아이디어, 복잡도가 파일 상단에 명시되었는가?
- [ ] 기술적 구현보다 알고리즘 의도와 도메인(문제 해결 전략)이 우선적으로 보이는가?

---

## 2. Explicit & Intentional Code (명시적이고 의도적인 코드)

코드는 "어떻게(How)"보다 **왜(Why)** 이 알고리즘/자료구조를 선택했는지, 어떤 제약과 트레이드오프가 있는지를 드러낸다.  
잘못된 상태를 매번 검증하는 대신, 애초에 **잘못된 상태를 표현할 수 없게 모델링**한다.  
명시성은 가독성과 AI 추론 품질의 핵심이다 — AI는 전체 프로젝트를 모두 읽지 못하므로 국부적으로 이해 가능한 코드가 특히 중요하다.

### Explicit & Intentional 체크리스트
- [ ] 핵심 아이디어, 복잡도, 트레이드오프가 주석으로 명시되었는가?
- [ ] early return, guard clause 등을 통해 흐름이 예측 가능한가?
- [ ] 가까운 코드만 읽어도 동작을 이해할 수 있는가?
- [ ] 잘못된 상태를 타입 수준에서 표현할 수 없도록 설계했는가?

---

## 3. Readability as Primary Quality (가독성을 최우선 품질로)

읽기 쉬운 코드는 유지보수 비용, 버그 발생률, 리뷰 비용, AI hallucination 가능성을 낮춘다.  
관련 로직은 가까이 배치하여 cognitive jump를 최소화한다.  
함수/변수 이름은 **의도, 역할, 알고리즘 의미, side effect 가능성**을 드러낸다.  
주석은 "왜(Why) 그렇게 설계했는지", "어떤 tradeoff가 있는지"를 설명한다. 코드가 하는 일(How)은 코드 자체가 설명한다.

### Readability 체크리스트
- [ ] 함수와 변수 이름이 의도, 역할, 알고리즘 의미를 명확하게 표현하는가?
- [ ] 관련 로직이 가까이 배치되어 cognitive jump가 최소화되었는가?
- [ ] 주석이 "왜(Why)"와 트레이드오프를 설명하는가?

---

## 4. Complexity Control & Simplicity (복잡도 제어와 단순성)

복잡성은 **상태(state) → 결합도(coupling) → 분기(branching) → 코드량(code volume)** 순으로 줄인다.  
모든 추상화는 유지보수 비용을 가진다. 인지 부하를 줄이고 변경 비용을 낮출 때만 추상화한다.  
작은 지역 중복(local duplication)은 조급한 추상화(premature abstraction)나 불안정한 공유 로직보다 안전하다.  
함수는 작고 단일 책임으로 유지 (가능하면 30~60줄 이내). 중첩 깊이는 3을 넘지 않도록 early return 적극 활용.

### Complexity Control 체크리스트
- [ ] 함수 크기가 적당하고 단일 책임인가?
- [ ] 중첩 깊이가 3을 넘지 않는가?
- [ ] 불필요한 추상화나 과도한 generic/template을 피했는가?
- [ ] 복잡성을 상태 → 결합도 → 분기 → 코드량 순으로 줄였는가?

---

## 5. Changeability & Refactoring (변경 용이성과 리팩토링)

가장 중요한 것은 "완벽한 설계"가 아니라 **안전하게 진화 가능한 구조**이다.  
리팩토링은 일상적인 활동이다. 코드를 만질 때마다 이름 개선, ambiguity 제거, dead code 제거, 책임 분리, 흐름 단순화를 수행한다.  
행동은 유지한 채 구조만 개선하고, 작은 단계로 안전하게 리팩토링한다.

### Changeability 체크리스트
- [ ] 행동(동작)을 유지하면서 구조만 개선했는가?
- [ ] 작은 단계로 리팩토링하기 좋은 구조인가?
- [ ] naming, 중복, 흐름이 지속적으로 개선될 여지가 있는가?

---

## 6. Consistency & Predictability (일관성과 예측 가능성)

비슷한 문제는 비슷한 패턴과 naming convention으로 해결한다.  
C++과 Java 파일을 `cpp/`와 `java/` 디렉토리로 명확히 분리한다.  
기계적으로 검사 가능한 것은 자동화한다. formatter, linter, static analysis는 자동화하고 인간은 알고리즘 정확성과 설계 판단에 집중한다.

### Consistency 체크리스트
- [ ] 비슷한 알고리즘 간 naming과 구조가 일관적인가?
- [ ] 언어별 파일이 명확히 분리되어 있는가?
- [ ] 자동화 가능한 검사(format, lint)는 자동화되었는가?

---

## 7. Exception Handling (예외 처리)

예외는 허용되지 않은 상태가 발생했을 때 반응하는 수단이다. 잘못 다루면 디버깅과 신뢰성 양쪽에 영향을 준다.

- 에러는 침묵하지 않는다. 의미 없는 기본값으로 덮거나 조용히 무시하는 것은 문제를 숨기는 Anti-Pattern이다.
- assert는 프로덕션 입력 검증 수단이 아니다. 개발자 불변식(invariant)을 선언하는 용도로 개발/테스트 환경에서만 사용한다.
- 예외를 던질 때는 반드시 제대로 된 Exception 객체를 사용한다. 단순 문자열이나 primitive를 throw하면 스택 트레이스가 사라진다.
- C++에서 타입 캐스팅은 런타임 검사를 동반해야 한다 (`dynamic_cast` + null 체크).
- switch/match 문에는 항상 default 케이스를 포함한다.
- 예외는 진짜 예외적인 상황에만 사용한다. 예측 가능한 분기는 조건문으로 처리한다.

### Exception Handling 체크리스트
- [ ] 에러를 침묵시키거나 의미 없는 기본값으로 대체하지 않았는가?
- [ ] assert와 validation을 용도에 맞게 구분했는가?
- [ ] switch/match에 default 케이스가 있는가?
- [ ] 예외를 흐름 제어에 사용하지 않았는가?

---

## 8. Performance & Resource Safety (성능과 리소스 안전성)

성능 최적화는 측정 기반이어야 한다. 실제 병목을 파악한 후 해당 부분만 선택적으로 개선한다.  
알고리즘 복잡도는 신중하게 선택하고, O(n²) 이상은 명확한 이유가 있어야 한다.  
공유 가변 상태(shared mutable state)는 최대한 피한다 — race condition과 테스트 신뢰성 저하를 유발한다.  
획득한 모든 리소스(메모리, 파일, 소켓 등)는 즉시 해제하여 리소스 누수를 방지한다.

### Performance & Safety 체크리스트
- [ ] 복잡도가 주석에 명시되고, 불필요한 고비용 연산을 피했는가?
- [ ] C++에서 리소스 관리가 안전한가? (smart pointer, RAII)
- [ ] Java에서 불변 객체와 적절한 컬렉션을 사용했는가?
- [ ] 공유 가변 상태를 최소화했는가?

---

## 9. Defensive Programming & Boundary Validation (방어적 프로그래밍과 경계 검증)

입력 경계(Trust Boundary)에서만 철저히 검증한다. 내부 레이어에서 동일한 검증을 반복하지 않는다.  
Assertion(프로그래머 가정), Validation(외부 입력), Domain Error(알고리즘 제약 위반)를 명확히 구분한다.  
Happy Path를 쉽게 읽을 수 있게 하고, 에러 처리 로직은 분리한다.  
Null 대신 명시적인 Optionality(`std::optional<T>`, `Optional<T>`)를 선호한다.

### Defensive Programming 체크리스트
- [ ] Trust Boundary(입력 지점)에서만 검증하고 내부에서 중복 검증하지 않았는가?
- [ ] Assertion, Validation, Domain Error를 명확히 구분했는가?
- [ ] null/nullptr 대신 명시적 Optional 타입을 사용했는가?
- [ ] Invalid state가 타입 수준에서 생성되지 않도록 설계했는가?

---

## 10. Testing (테스트)

테스트 코드도 생산 코드와 동일한 수준으로 깨끗하고 읽기 쉽게 작성한다.  
각 구현에는 문제 예제 입력/출력과 예상 결과를 포함한다.  
테스트 이름은 알고리즘 동작을 명확히 드러낸다.  
가능한 한 TDD를 활용하고, 레거시 코드 수정 전에는 Characterization Test를 먼저 추가한다.

### Testing 체크리스트
- [ ] 예제 입력/출력과 테스트 케이스가 포함되었는가?
- [ ] 테스트 이름이 알고리즘 동작을 명확히 표현하는가?
- [ ] 테스트가 구현 세부사항이 아닌 관찰 가능한 결과를 검증하는가?
- [ ] 테스트 코드가 생산 코드 수준으로 읽기 쉬운가?

---

## 11. Change Process (작업 흐름)

**기본 작업 흐름**:
1. 변경할 알고리즘 의도와 영향을 충분히 이해한다
2. Preparatory Refactoring으로 구조 개선
3. 최소한의 기능 변경 수행
4. 테스트 추가/갱신
5. Boy Scout Rule 적용하여 전체 정리

### Change Process 체크리스트
- [ ] 변경 범위가 명확히 정의되었는가?
- [ ] 기존 테스트가 여전히 통과하는가?
- [ ] Boy Scout Rule(왔을 때보다 깨끗하게)을 적용했는가?

---

## 언어별 실천 가이드

### C++ Specific

#### 기본 형식 및 파일 구조
- **C++20**을 타겟으로 한다 (C++23 기능은 사용 금지).
- 모든 `.h` 파일은 **self-contained** 해야 하며, `#define` guard를 사용한다. (예: `ALGORITHM_SORT_MERGESORT_H_`)
- Include 순서: Related header → C system headers → C++ standard library headers → other libraries → project headers
- **Forward declaration**은 최소화하고, `Include What You Use` 원칙을 따른다.
- Indentation: **2 spaces** (Google Style 기본)
- Line length: **80자** 권장 (최대 100자 이내)

#### Naming Conventions
- **파일명**: 소문자 + underscore (`merge_sorted_arrays.cc`, `detect_cycle_floyd.h`)
- **Class / Struct / Type**: UpperCamelCase (`MergeSortedArrays`, `UnionFind`)
- **Function / Method**: UpperCamelCase (`Solve`, `DetectCycle`, `MergeTwoArrays`)
- **Variable / Parameter**: lower_snake_case (`current_node`, `max_heap`)
- **Constant**: `k` + UpperCamelCase (`kModulo`, `kMaxCapacity`)
- **Namespace**: lower_snake_case (`namespace algorithm { ... }`)

#### Modern C++ Usage & Safety
- Raw pointer 대신 **smart pointer** (`std::unique_ptr`, `std::shared_ptr`)와 reference를 우선 사용
- RAII, `std::optional`, range-based for, structured bindings, `constexpr` 등을 적극 활용
- `using namespace std;`는 **파일 내에서 제한적으로**만 사용 (global scope 금지)
- Template는 알고리즘 일반화가 명확할 때만 사용하며, 과도한 metaprogramming은 피한다
- `std::move`, `std::forward` 등 ownership transfer를 명확히 표현
- `nullptr` 사용 (NULL, 0 사용 금지)

#### Comments (Doxygen-style)
- 알고리즘 핵심 아이디어, 시간/공간 복잡도, 트레이드오프를 파일/함수 상단 주석으로 반드시 설명
- "왜(Why)" 이 접근법을 선택했는지 기술

#### C++ Specific 체크리스트
- [ ] Google C++ Style Guide의 Naming, Formatting, Include 규칙을 따랐는가?
- [ ] Header 파일이 self-contained이며 `#define` guard를 사용했는가?
- [ ] Include 순서와 `Include What You Use`를 준수했는가?
- [ ] Raw pointer 대신 smart pointer / RAII를 적절히 사용했는가?
- [ ] Template와 Modern C++ 기능이 가독성을 해치지 않도록 사용했는가?
- [ ] Line length, indentation, namespace 사용이 일관적인가?
- [ ] `nullptr`를 사용했는가? (NULL, 0 금지)

### Java Specific

#### 파일 구조 및 기본 형식
- 하나의 소스 파일에 **하나의 public top-level 클래스**만 선언
- 파일 인코딩: **UTF-8**
- Package declaration → Imports → Class 순서 준수
- **Wildcard import 금지** (`import java.util.*;`)
- Imports는 static / non-static 그룹으로 나누고 ASCII 정렬
- Indentation: **4 spaces** (Tab 사용 금지)
- Column limit: **100자** (가능하면 80~100자 이내 권장)
- Braces: K&R 스타일 (여는 괄호는 같은 줄, 닫는 괄호는 새 줄)

#### Naming Conventions
- Class: UpperCamelCase (e.g. `MergeSortedArrays`, `DetectCycleFloyd`)
- Method / Variable / Parameter: lowerCamelCase
- Constant: UPPER_SNAKE_CASE (의미 완전하게, e.g. `MODULO_1_000_000_007`)
- Boolean 변수: `isSuccess` 대신 `success` 또는 `hasCycle` 등 의미 명확한 이름 권장
- Package: 모두 소문자, 의미 있는 단어 (e.g. `com.mimul.algorithm.sort`)

#### Code Style
- `long` 리터럴은 `L` 대문자 사용 (e.g. `1000000000L`)
- `equals()` 호출 시 null-safe하게: `"constant".equals(variable)` 또는 `Objects.equals()`
- Collection: `toArray(new Type[size])` 형태 권장, `subList` 수정 주의
- Record (Java 14+)를 불변 DTO나 결과 객체에 적극 활용
- Stream API: 가독성이 **명확히 향상**될 때만 사용. 복잡해지면 전통 for-loop 선호
- `Optional`을 활용하여 명시적 Optionality 표현

#### Javadoc & Comments
- Public API나 중요한 메서드에는 Javadoc 작성
- 알고리즘 핵심 아이디어, 시간/공간 복잡도, 트레이드오프는 Javadoc 또는 구현 주석으로 반드시 설명

#### Java Specific 체크리스트
- [ ] 파일 구조 (Package → Import → Class)와 Google Style formatting을 따랐는가?
- [ ] Naming 규칙 (UpperCamelCase / lowerCamelCase / UPPER_SNAKE_CASE)을 일관되게 적용했는가?
- [ ] Magic number를 상수로 정의했는가?
- [ ] Indentation 4 spaces, column limit 100자, wildcard import 없음
- [ ] Record, Stream 등 현대 Java 기능을 가독성 향상에 적절히 사용했는가?
- [ ] equals()/hashCode()가 필요한 클래스에서 올바르게 처리했는가?

---

## 금지하는 Anti-Patterns

**코드 레벨 (강력 금지)**

- **Anemic Code** — 데이터만 있고 알고리즘 의도(행동)가 거의 없는 함수/클래스
- **God Function / God Class** — 너무 많은 책임을 가져 이해와 유지보수가 어려운 코드
- **Primitive Obsession** — 알고리즘 개념을 int, string 등 기본 타입만으로 표현하는 것
- **Feature Envy** — 메서드가 자신의 데이터보다 다른 클래스의 데이터에 과도하게 의존하는 코드
- **Deep Nesting** — 과도하게 깊은 코드 중첩 (3레벨 초과)
- **Shared Mutable State** — 공유 가변 상태로 인한 암묵적 의존과 예측 불가능한 버그
- **Silent Failure** — 예외를 잡아서 무시하거나 의미 없는 기본값으로 대체하는 행위
- **Magic Numbers / Magic Strings** — 의미 없는 리터럴 값을 직접 코드에 사용하는 것
- **Excessive Null Checking** — null을 기본값으로 남용하고 과도한 null 체크가 난무하는 설계
- **Utility Dumping Grounds** — 의미 없는 `Utils`, `Helper`, `Common` 클래스에 로직을 마구 넣는 행위

**설계 / 구조 레벨**

- **Speculative Abstraction** — 실제 필요하지 않은 미래를 위한 과도한 추상화
- **Deep Inheritance** — 과도한 상속 계층 구조
- **Meta-programming Abuse** — 과도한 메타프로그래밍과 리플렉션 남용
- **Inconsistent Naming** — 일관성 없는 명명 규칙
- **C++ 전역 `using namespace std;`** — 이름 충돌과 가독성 저하

---

## 실패 패턴들

알고리즘 프로젝트에서 반복되는 구조적 실패 패턴이다.

**1. 바퀴의 재발명**

- STL(`std::sort`, `std::queue`, `std::unordered_map`)이나 Java Collections로 충분히 해결 가능한 것을 처음부터 직접 구현하는 것.
- 정렬, 해시, 큐 등 표준 라이브러리가 제공하는 것을 "연습"이라는 이유로 프로덕션 코드에 직접 넣는 것.

**2. Over Engineering**

- 알고리즘 하나를 구현하면서 불필요한 팩토리 패턴, 전략 패턴, 레이어 구조를 도입하는 것.
- 단순한 탐색 알고리즘에 템플릿 메타프로그래밍을 과도하게 적용하는 것.

**3. 땜방식 대응**

- 경계값 오류(Off-by-one, 빈 배열, 최대값, 인덱스 범위 초과)를 고려하지 않고 예제만 통과시키는 것.
- null/nullptr/None 체크 없이 배포하거나 원인을 두고 증상만 방어하는 것.
- 테스트가 깨지면 기대값을 실제 잘못된 구현에 맞춰 수정하는 것.
- 복잡도(Complexity)를 주석으로 명시하지 않고 "일단 동작하면 된다"로 넘기는 것.

**4. 너무 이른 추상화**

- 구현이 하나뿐인데 Strategy 패턴이나 인터페이스 계층을 미리 만드는 것.
- 두 가지 유사한 알고리즘을 공통 추상화로 묶으려다 양쪽 구현이 모호해지는 것.

**5. "한 번 데였다고 다 금지하는" 반지성적 규칙**

- 과거에 template 코드에서 버그가 났다고 template 사용을 팀 전체에서 금지하는 것.
- Stream API로 한 번 복잡해진 경험이 있다고 모든 컬렉션 처리를 for-loop으로 강제하는 것.
- Optional 사용을 금지하고 null 체크를 호출 쪽에 모두 떠넘기는 것.

---

**요약 체크리스트 (Code Review / AI Prompt용)**

- [ ] Domain Clarity와 Explicit Intent가 최우선으로 지켜졌는가?
- [ ] 가독성과 단순성이 유지되었는가?
- [ ] 알고리즘 복잡도와 트레이드오프가 주석에 명시되었는가?
- [ ] 예외 처리와 경계 검증이 올바르게 이루어졌는가?
- [ ] 테스트/예제가 포함되었는가?
- [ ] 언어별 idiom을 적절히 따랐는가?
- [ ] Anti-Pattern을 피했는가?
