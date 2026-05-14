---
name: Algorithm Project Test Style Guide
description: C++/Java 알고리즘 프로젝트에서 Classicist TDD 철학을 기반으로 AI 테스트 코드 생성·리뷰 시 적용할 테스트 작성 표준과 품질 기준
---

# Algorithm Project Test Style Guide

본 문서는 Algorithm 프로젝트(C++ + Java 혼합)의 테스트 철학과 작성 표준을 정의한다. Classicist TDD 철학을 알고리즘/자료구조 중심 프로젝트에 적용한다.

---

# 목표

- 구현 세부사항이 아닌 관찰 가능한 동작(Behavior)과 불변식(Invariant) 검증
- 리팩토링에 강하고 유지보수하기 좋은 테스트
- 빠르고, 격리되고, 결정적인 테스트
- 알고리즘의 정확성과 안정성을 지속적으로 검증
- AI 생성 테스트의 높은 일관성과 품질 확보

---

## 테스트 철학

아래 6가지 원칙이 이 문서의 모든 규칙의 근거다. 이 철학을 먼저 받아들이지 않으면 개별 규칙은 근거 없는 제약으로 느껴질 수 있다.

1. **구현이 아닌 동작을 테스트한다.** 순수 리팩토링(메서드명 변경, 반복문 → 재귀 등)은 테스트를 깨뜨려서는 안 된다. 리팩토링 후 테스트가 깨진다면, 그 테스트는 동작이 아닌 구현을 잠근 것이다.

2. **시스템 경계에서만 모킹한다. 경계 안쪽은 전부 실제다.** "내가 제어할 수 없는 것"(외부 API, 시스템 시계, 난수)만 대체하고, 내가 소유한 코드는 실제 구현을 사용한다.

3. **Classicist(Chicago) TDD를 선호한다.** Mockist 방식은 AI가 내부를 리팩토링할 때마다 구현에 결합된 테스트가 깨진다. Classicist 방식은 최종 상태와 반환값을 검증하므로 AI가 내부를 어떻게 바꾸든 사양을 만족하면 통과한다.

4. **알고리즘 프로젝트에서 단위 테스트가 우선이다.** 순수 계산 로직이 중심이므로 DB나 외부 의존성 없이 빠른 단위 테스트가 핵심이다. 통합 테스트는 I/O나 환경 의존 로직에만 사용한다.

5. **테스트는 빠르고(Fast), 격리되어 있으며(Isolated), 결정적(Deterministic)이어야 한다.** 셋 중 하나라도 무너지면 테스트 스위트 전체에 대한 신뢰가 흔들린다.

6. **의미 있는 소수의 테스트가 신뢰도 낮은 다수보다 가치 있다.** 테스트를 추가하기 전에 "이 테스트가 없으면 실제 버그가 통과할 수 있는가"를 먼저 묻는다. 그 답이 "아니오"라면 추가하지 않는 것이 낫다.

---

## Classicist vs Mockist — AI 시대의 선택

**Mockist(London School)** 는 협력 객체 간 상호작용을 검증한다. AI가 코드를 리팩토링하거나 재생성할 때 내부 구현이 달라지면, 동작이 동일해도 Mockist 테스트는 깨진다. AI는 메서드 호출 패턴에서 테스트를 역으로 추론하기 때문에 생성된 테스트가 구현과 1:1로 결합되는 경향이 있다.

**Classicist(Chicago School)** 는 최종 상태와 반환값을 검증한다. AI가 내부를 어떻게 바꾸든 사양을 만족하면 통과한다.

**선택 기준**: 검증하려는 동작의 성격에 따라 결정한다.
- 알고리즘 로직, 상태 변화 → **상태 검증** (Classicist, 기본값)
- 외부 side-effect (이벤트 발행, 외부 API 호출) → 상호작용 검증이 불가피한 경우에만 허용

---

# 1. Think Before Testing

테스트를 작성하기 전에 반드시 알고리즘/자료구조의 본질을 먼저 이해한다.

반드시 먼저 고민할 것:

- 이 컴포넌트의 핵심 Invariant(불변식)은 무엇인가?
- 어떤 입력에서 동작이 깨질 가능성이 있는가?
  - Boundary
  - Edge Case
  - Pathological Input
- 리팩토링 후에도 이 테스트가 살아남을 수 있는가?
- 이 테스트가 알고리즘의 올바른 동작을 잘 설명하는가?
- 이 테스트가 구현이 아닌 관찰 가능한 결과를 검증하는가?

---

## 대표적인 Invariant 예시

### Ordering Invariant
- 정렬 결과가 항상 오름차순/내림차순 유지

### Size / Capacity Invariant
- 자료구조 크기가 음수가 되지 않음
- capacity 초과 금지

### Connectivity Invariant
- Union-Find 연결 상태 유지

### Structural Invariant
- Heap parent-child 관계 유지
- Tree balance 유지

### Uniqueness Invariant
- 중복 허용 여부 유지

### Idempotency Invariant
- 동일 연산 반복 시 결과 안정성 유지 (`sort(sort(arr)) == sort(arr)`)

### Monotonicity Invariant
- 입력 증가 시 특정 값이 감소하지 않음

---

## C++ / Java Specific

### C++
- Google Test (gtest)
- Google Mock (최소 사용)

### Java
- JUnit 5
- AssertJ 적극 권장

---

## 체크리스트 (Think Before Testing)

- [ ] 핵심 Invariant를 명확히 정의했는가?
- [ ] Edge case와 Failure scenario를 고려했는가?
- [ ] 테스트가 구현이 아닌 Behavior를 검증하는가?
- [ ] 적절한 테스트 프레임워크를 선택했는가?

---

# 2. Behavior & State Verification First

테스트는 구현 방식(How)이 아닌 관찰 가능한 결과(What)와 최종 상태를 검증한다.

---

## 권장 패턴

### AAA 패턴 철저 준수

- **Arrange**: 테스트에 필요한 상태와 의존성을 준비한다.
- **Act**: 검증 대상 동작을 **정확히 한 번**만 실행한다.
- **Assert**: 관찰 가능한 결과를 검증한다.

세 단계가 뒤섞여 있다면 한 번에 여러 가지를 검증하려 하거나 구조가 잘못된 것이다.

### 우선 검증 대상

- 최종 결과 (함수 반환값)
- 핵심 상태 변화
- Observable Behavior
- 핵심 Invariant

---

## 지양할 것

- private 메서드 호출 검증
- 내부 helper 호출 횟수 검증
- loop 순서 검증
- intermediate state 과도 검증
- 특정 자료구조 사용 강제

---

## 상호작용 검증이 허용되는 예외

상호작용 검증(`verify`, `EXPECT_CALL` 등)은 다음 경우에만 허용한다:

- Side-effect 자체가 요구사항인 경우 (이벤트 발행, 외부 API 호출 등)
- 외부 시스템과의 호출 계약을 검증해야 하는 경우
- retry, idempotency key 전달 등 호출 정책 자체가 중요한 계약인 경우

---

## Bad vs Good 예시

**Bad — 구현에 강하게 결합:**

```cpp
// 내부 비교 함수 호출 횟수를 검증 (지양)
EXPECT_CALL(mock_compare, Compare(_, _)).Times(AtLeast(1));
```

```java
// 구현 세부사항 검증 (지양)
verify(internalHelper).process(input);
```

**Good — 결과 상태에 집중:**

```cpp
TEST(QuickSortTest, quickSort_sortsArrayCorrectly_whenInputIsUnsorted) {
    std::vector<int> input = {3, 1, 2};
    QuickSort(input);
    EXPECT_THAT(input, ElementsAre(1, 2, 3));
}
```

```java
@Test
void binarySearch_returnsNegativeOne_whenKeyDoesNotExist() {
    int result = BinarySearch.search(new int[]{1, 2, 3}, 99);
    assertThat(result).isEqualTo(-1);
}
```

---

## 체크리스트 (Behavior & State Verification)

- [ ] AAA 패턴을 명확하게 따랐는가?
- [ ] 내부 구현 세부사항이 아닌 결과와 상태를 검증하는가?
- [ ] 중요한 Invariant를 검증했는가?
- [ ] 상호작용 검증을 사용한 경우, 허용 예외에 해당하는가?
- [ ] 합법적인 리팩토링을 방해하지 않는가?

---

# 3. Mocking Strategy (Minimal Mocking)

알고리즘 프로젝트에서는 Mock 사용을 최소화한다.

**핵심 원칙**: 내가 통제하지 못하거나 비결정적인 것만 대체하고, 나머지는 실제 구현을 사용한다.

---

## Mock vs Stub vs Fake — 정확히 구분하자

- **Mock** — 상호작용 검증 특화. "이 메서드가 이 인자로 N번 호출되었는가?"를 확인한다. 과도하게 사용하면 over-specification이 발생해 리팩토링 내성을 떨어뜨린다.
- **Stub** — 고정된 응답만 반환한다. 호출 여부는 검증하지 않는다.
- **Fake** — 실제로 동작하는 간단한 구현체(예: in-memory 자료구조). 상태 기반 검증과 가장 잘 어울린다.

**권장 방향**: Mock을 최대한 줄이고, **Fake와 실제 구현**을 늘린다.

---

## 모킹 경계 3층

1. **프로세스 경계** (네트워크, 외부 시스템) → 반드시 대체해야 한다.
2. **시간/환경 경계** (시스템 시계, 파일 시스템, 난수 생성기) → 제어 불가능하므로 대체한다.
3. **논리적 경계** (레이어, 모듈, 같은 코드베이스 내 협력 객체) → 테스트 의도와 범위에 따라 결정하며, 기계적으로 Mock을 붙이지 않는다.

---

## Test Double 허용 기준

내부 의존성을 Test Double로 대체하는 것은 다음 세 조건 중 **하나 이상**에 해당할 때만 허용한다:

1. **비결정성 제거** — 시스템 시계, 난수 등 테스트를 불안정하게 만드는 요소
2. **재현 불가능한 실패 시뮬레이션** — 실제 환경에서 만들기 어려운 에러 케이스
3. **피드백 루프 단축** — 순수 계산 로직 검증 시 외부 의존성 기동 비용이 과도할 때

이 세 조건에 해당하지 않는데 내부 의존성을 대체하면, 테스트 범위만 좁아질 뿐 신뢰도는 높아지지 않는다.

---

## Mock 허용 대상

### Non-deterministic 요소
- Random, System Time, UUID, 외부 Clock

### 외부 I/O (본 프로젝트에서는 매우 제한적)
- 파일 시스템, 네트워크, DB, 외부 API

---

## Mock 금지 대상

- 순수 함수
- Value Object
- Algorithm 핵심 클래스
- 대부분의 Helper 클래스
- Collection / Container 성격 객체

---

## 비결정성은 회피가 아니라 소스에서 제거하라

비결정적 요소를 테스트 내에서만 피하는 것이 아니라, **소스 코드 자체에서 제거**하는 것이 근본 해결책이다.

- 시스템 시계 → 주입 가능한 Clock 인터페이스로 교체
- UUID/난수 생성기 → 테스트에서 제어할 수 있도록 주입 구조로 변경

---

## 권장 방법

### C++

```cpp
std::mt19937 rng(1234);  // seed 고정으로 결정적 동작 보장
```

### Java

```java
Clock.fixed(Instant.parse("2024-01-01T00:00:00Z"), ZoneOffset.UTC)
new Random(1234)  // seed 고정
```

---

## 체크리스트 (Mocking Strategy)

- [ ] Mock이 아닌 Fake 또는 실제 구현으로 대체 가능한가?
- [ ] Test Double 허용 기준(3가지) 중 하나 이상에 해당하는가?
- [ ] 비결정성을 소스 코드 수준에서 제거했는가? (주입 구조)
- [ ] 내부 구현을 Mock하지 않았는가?

---

# 4. Test Naming Convention

---

## 템플릿

```text
<행동>_<기대결과>_when<조건>
```

---

## 나쁜 이름 vs 좋은 이름

**나쁜 이름 — 구현 중심 또는 의미 불명확:**

```text
test_sort_called()
should_work()
handles_input()
test_binarySearch()
```

**좋은 이름 — 행동 중심:**

```text
quickSort_sortsArrayCorrectly_whenInputIsUnsorted
binarySearch_returnsNegativeOne_whenKeyDoesNotExist
ringBuffer_dropsOldestElement_whenFullAndNewPush
stack_preservesLIFOOrder_afterMultipleOperations
```

---

## 권장 동사

- returns, throws, preserves, maintains, rejects, fails, sorts, removes, inserts, updates

**피해야 할 모호한 동사**: `works`, `handles`, `processes`, `tests`, `checks`

---

## 체크리스트 (Test Naming)

- [ ] action_expected_when_condition 패턴을 따르는가?
- [ ] 이름만으로 테스트 의도를 이해할 수 있는가?
- [ ] 동사 사용이 일관적이며 모호한 표현을 피했는가?

---

# 5. 테스트 피라미드

테스트 비용, 피드백 속도, 결함 수정 비용에서 출발한 실용적 전략이다.  
결함을 단위 테스트 단계에서 발견할 때 수정 비용을 1로 본다면, 프로덕션 배포 후에는 최대 **100배**까지 증가한다.

---

## 권장 구조

| 레이어 | 목적 | 권장 비율 |
|--------|------|-----------|
| **Unit** | 알고리즘, 경계 조건, 복잡한 분기 | 많음 (비자명한 로직에 집중) |
| **Integration** | I/O 의존 로직, 외부 의존성 연결 | 유스케이스당 1~3개 |
| **E2E** | 핵심 동작 경로 | 경로당 1개 |

Google 권장 비율: **단위 70 : 통합 20 : E2E 10**  
알고리즘 프로젝트는 순수 계산 로직 중심이므로 **단위 테스트 비율이 더 높다**.

---

## 언제 테스트를 생략할 수 있는가

- 복잡한 분기가 없는 단순 래퍼(wrapper)는 통합 테스트 1개로 충분하다. (단, 최소 1개는 유지)
- 언어 표준 라이브러리(STL, Java Collections)의 동작 자체는 테스트하지 않는다. 우리 코드가 올바르게 사용하는지만 검증한다.
- 정적 상수 값은 타입 시스템으로 충분히 검증된다.

## 반드시 테스트해야 하는 것

- 핵심 알고리즘 로직과 복잡한 분기
- 경계 조건 (edge case, off-by-one)
- 과거에 버그가 발생했던 경로 (**Regression Test**: 버그 재현 테스트 먼저 → 실패 확인 → 수정)
- 핵심 Invariant Property 테스트

---

## 성능 목표

통합 테스트 하나당 **100~300ms** 수준을 목표로 한다.
- 전체 상태 초기화보다 **트랜잭션 롤백** 우선 사용
- 비용이 큰 외부 연동 테스트는 환경 변수(`LIVE_TEST=true`)로 분리

---

## 체크리스트 (테스트 피라미드)

- [ ] 단위 테스트가 충분히 많고 빠른가?
- [ ] 통합 테스트가 실제 I/O 의존성을 검증하는가?
- [ ] 테스트 피라미드 구조를 역전시키지 않았는가? (단위 < 통합 금지)

---

# 6. Property-Based Testing (강력 권장)

알고리즘 프로젝트에서는 Property-based testing을 적극 권장한다.

---

## 추천 도구

### C++
- RapidCheck

### Java
- jqwik

---

## Example-based Test vs Property-based Test

### Example-based Test

목적:
- 특정 시나리오 설명
- 회귀 버그 재현
- 문서 역할
- 읽기 쉬운 사례 제공

### Property-based Test

목적:
- 광범위 입력 검증
- Invariant 자동 검증
- Edge 조합 탐색
- 예측하지 못한 입력 탐색

Property test는 Example test를 **대체하지 않는다**. 두 방식은 상호보완적으로 사용한다.

---

## 언제 전환을 고려해야 하는가

같은 규칙을 검증하는 예시 테스트를 **4개 이상 작성하려는 순간**, Property-based test로 전환을 진지하게 검토한다.

---

## Property 예시

### Sorting

- 결과는 항상 정렬되어야 한다.
- 입력 원소 수가 보존되어야 한다.
- permutation이 유지되어야 한다. (멱등성: `sort(sort(arr)) == sort(arr)`)

### Stack

- push 후 pop 시 마지막 값 반환
- size invariant 유지

### Graph

- BFS/DFS 방문 노드 중복 없음

---

## Shrinking — Property-Based Testing의 핵심 강점

테스트 실패 시 도구가 **최소 재현 케이스(Minimal Reproducible Example)** 를 자동으로 찾아준다. 수천 개의 무작위 입력 중 하나가 실패해도, 입력을 최대한 단순화하여 원인을 빠르게 파악할 수 있다.  
RapidCheck, jqwik 모두 기본적으로 Shrinking을 제공한다.

---

## Generator 품질이 테스트 품질을 결정한다

좋은 Generator는 단순 무작위가 아니라:
- 도메인 제약 조건을 반영하고
- 엣지 케이스(0, 최대값, 빈 배열, 음수)를 의도적으로 포함해야 한다.

**AI의 한계**: AI는 구체적인 예시 테스트는 잘 만들지만 **invariant(불변 조건)를 스스로 정의하는 데 취약**하다. invariant 정의는 개발자가 도메인을 깊이 이해하고 채워넣어야 하는 영역이다.

---

## 체크리스트 (Property-Based Testing)

- [ ] 중요한 Invariant를 property test로 검증했는가?
- [ ] Example-based test와 적절히 조합했는가?
- [ ] Generator가 엣지 케이스를 의도적으로 포함하는가?
- [ ] 랜덤 입력이 deterministic하게 제어되는가? (seed 고정)

---

# 7. Test Data Selection Strategy

입력 데이터 선택은 알고리즘 테스트 품질의 핵심이다.

---

## 반드시 고려할 입력 패턴

- 이미 정렬된 입력
- 역순 입력
- 중복값 포함
- 모든 값 동일
- 단일 원소
- 최대 크기 입력
- 랜덤 입력
- pathological input
- sparse data
- dense data

*(빈 입력, Null/Zero 입력, Overflow/Underflow 케이스는 Section 12에서 별도 다룬다)*

---

## 대표적인 Pathological Input 예시

### QuickSort
- 이미 정렬된 입력
- pivot worst-case 입력

### Hash
- collision 유도 입력

### Graph
- cycle graph
- disconnected graph

### Heap
- duplicate priority

---

## 체크리스트 (Test Data Selection)

- [ ] 정상 입력과 비정상 입력을 모두 포함했는가?
- [ ] worst-case 입력을 고려했는가?
- [ ] pathological input을 검토했는가?

---

# 8. Test Structure & Organization

---

## 반드시 테스트해야 할 것

- Boundary condition
- Edge case
- Failure scenario
- 모든 주요 Invariant
- Regression bug
- Invalid input

---

## Boundary 예시

- 0, 1, MAX, MIN, empty, full, null

---

## Regression Test 원칙

버그 수정 시 반드시:

1. 실패하는 테스트를 먼저 추가한다.
2. 버그 재현 입력을 최소화한다.
3. 버그 원인을 설명하는 이름을 사용한다.
4. 동일 유형 버그의 인접 케이스도 검토한다.

---

## 체크리스트 (Test Structure)

- [ ] Boundary와 Edge Case를 충분히 커버했는가?
- [ ] Regression Test가 포함되었는가?
- [ ] 불필요한 중복 테스트가 없는가?
- [ ] 테스트 구조가 읽기 쉬운가?

---

# 9. Parameterized Testing

반복되는 입력 패턴은 Parameterized Test를 우선 고려한다.

---

## C++

```cpp
TEST_P(...)
INSTANTIATE_TEST_SUITE_P(...)
```

---

## Java

```java
@ParameterizedTest
@CsvSource
@MethodSource
```

---

## 사용 목적

- 중복 제거
- 다양한 입력 조합 검증
- edge case coverage 강화
- 가독성 향상

---

## 체크리스트 (Parameterized Testing)

- [ ] 반복되는 테스트를 Parameterized Test로 통합했는가?
- [ ] 입력 variation이 충분한가?
- [ ] 가독성을 해치지 않는가?

---

# 10. Complexity-Aware Testing

알고리즘 테스트는 correctness뿐 아니라 성능 특성도 고려해야 한다.

---

## 검증 대상

- 비정상적인 O(n²) 퇴화
- 불필요한 메모리 증가
- 재귀 깊이 폭발
- 입력 증가 시 비선형 성능 악화

---

## 주의사항

절대 실행 시간 대신:

- 입력 증가에 따른 상대적 증가율
- complexity trend
- pathological input 대응

을 우선 검증한다.

---

## 금지 사항

- 환경 의존적인 timing assertion
- 불안정한 benchmark 기반 assertion

---

## 체크리스트 (Complexity Testing)

- [ ] worst-case complexity를 고려했는가?
- [ ] pathological input 성능을 검토했는가?
- [ ] flaky한 timing assertion이 없는가?

---

# 11. Flaky Test Prevention

> "Non-deterministic tests can completely destroy the value of an automated regression suite." — Martin Fowler

**Flaky 테스트는 절대 커밋하지 않는다.** 이미 들어왔다면 **최대한 빠르게 격리**해야 한다 (실무 기준: 24시간 이내).

---

## Flaky 테스트의 근본 원인

- 공유 전역 상태 (테스트 간 격리 부족)
- 실제 시스템 시계 직접 참조 (`LocalDateTime.now()`, `std::chrono::system_clock::now()`)
- 테스트 실행 순서 의존
- 시드 없는 난수 생성
- 네트워크나 외부 서비스 직접 의존
- 비동기 처리 미완료로 인한 Race Condition
- 병렬 실행 시 자원 충돌 (포트, 파일 공유)

---

## 격리(Quarantine) 프로세스

단순 `skip`/`ignore`/`@Disabled` 처리가 아닌, 반드시 다음을 포함해야 한다:

- **이슈 링크** + **담당자 지정** + **기한 설정** + **원인 가설 기록**
- 담당자와 가설이 없는 격리는 무의미하다. 그런 테스트는 과감히 삭제한다.
- 동일 테스트가 **2회 이상 반복 실패** 시 CI에서 자동으로 머지를 차단하는 규칙을 둔다.

**주 1회 격리된 테스트를 재활성화하여 시도한다.** 통과하면 정상 복귀, 실패하면 원인 재분석.

---

## 임시방편 금지

retry 루프, `sleep()` 삽입, 타임아웃 증가는 **증상 완화**일 뿐이다. 근본 원인을 해결하지 않은 채로 장기적으로 유지해서는 안 된다.

---

## Flaky 테스트는 아키텍처 개선의 신호

많은 flaky 테스트의 진짜 원인은 테스트 코드 자체가 아니라 **소프트웨어 설계 문제**에 있다. 시간 의존성, 전역 상태 공유, 외부 서비스 직접 의존이 대표적이다. Flaky가 늘어난다면 단순한 테스트 문제가 아니라 **아키텍처 개선이 필요하다는 경고**로 읽어야 한다.

---

## 제거 대상

- Random (seed 미고정)
- System Time (직접 참조)
- Global State
- Shared Mutable State
- 환경 의존 실행 순서

---

## 체크리스트 (Flaky Prevention)

- [ ] 모든 테스트가 deterministic한가?
- [ ] Random seed가 고정되었는가?
- [ ] Time 의존성을 주입 구조로 제거했는가?
- [ ] 테스트 순서 의존성이 없는가?
- [ ] Flaky 테스트에 격리 프로세스(이슈+담당자+기한+원인가설)를 적용했는가?

---

# 12. Edge & Failure Case Testing

---

## 반드시 검증할 것

- Empty 입력
- Null 입력
- Zero 입력
- Overflow
- Underflow
- Maximum capacity
- Invalid parameter
- Exception handling

---

## Floating Point Testing Rules

Floating Point 비교 시:

- `==` 비교를 지양한다.
- epsilon 기반 비교를 사용한다.
- 누적 오차 가능성을 고려한다.

---

## 예시

### C++

```cpp
EXPECT_NEAR(actual, expected, 1e-9);
```

### Java

```java
assertThat(actual).isCloseTo(expected, within(1e-9));
```

---

## 체크리스트 (Edge & Failure)

- [ ] Empty / Null / Zero 입력을 검증했는가?
- [ ] Overflow / Underflow를 검증했는가?
- [ ] Invalid parameter 처리를 검증했는가?
- [ ] Floating point 비교 규칙을 준수했는가?

---

# 13. Test Readability

테스트는 디버깅 문서다. 읽기 쉬운 테스트는 유지보수 비용을 크게 줄인다.

---

## 권장 사항

- 의미 있는 fixture 이름 사용
- Magic Number 제거
- assertion grouping 최소화
- 하나의 테스트는 하나의 핵심 behavior 집중
- 불필요한 setup 제거

---

## Assertion 원칙

좋은 Assertion은:

- 실패 원인이 명확해야 한다.
- 핵심 invariant를 직접 표현해야 한다.
- assertion 개수보다 의미가 중요하다.

---

## 체크리스트 (Readability)

- [ ] 테스트가 쉽게 읽히는가?
- [ ] assertion 의도가 명확한가?
- [ ] 불필요한 noise가 없는가?

---

# 14. AI Generated Test Review Rules & PR Red Flags

AI가 생성한 테스트라도 반드시 사람이 최종 검토한다.  
아래 Red Flag 기준을 AI 생성 테스트에 엄격하게 적용하면 **가치 없는 테스트를 효과적으로 걸러내는 필터**가 된다.

---

## 반려(Reject) — 즉시 수정 요청해야 할 신호

- 상호작용 검증만 있고 반환값이나 관찰 가능한 상태를 검증하지 않는 테스트
  - 단, Side-effect 자체가 비즈니스 요구사항인 경우는 예외
- 비공개(private) 메서드나 내부 구현에 직접 접근하는 테스트
- **Assertion이 없는 테스트** — CI는 통과하지만 아무것도 검증하지 않음
- 의미 없는 Assertion만 있는 경우 (`EXPECT_NE(result, nullptr)` 한 줄만 있는 등)
- 이슈, 담당자, 기한 없이 단순 `skip` / `@Disabled` 처리된 테스트
- 테스트 이름이 함수명이나 구현 구조를 그대로 반영하는 경우 (구현을 잠그는 신호)

---

## 주의 검토(Review) — 주의 깊게 살펴봐야 할 신호

- 상태 검증보다 상호작용 검증이 압도적으로 많은 경우
- 실제 assertion 코드에 비해 설정(setup)과 모킹 코드가 훨씬 많은 경우 (Fixture/Builder 패턴 필요 신호)

---

## AI 테스트 금지 패턴

- 의미 없는 `assertTrue(true)` / `EXPECT_TRUE(true)`
- 내부 메서드 호출 검증 남발
- 실제 behavior 없는 테스트
- 과도한 setup
- 우연히 통과하는 테스트

---

## AI의 한계와 인간의 역할

AI 도구는 위 목록의 나쁜 패턴을 매우 자주 생성한다:
- 과도한 Mock 사용
- 구현 구조를 그대로 따르는 테스트 이름
- 의미 없는 Assertion
- invariant를 스스로 정의하지 못함 (개발자가 채워야 하는 영역)

---

## 체크리스트 (AI Generated Tests)

- [ ] 테스트가 실제 behavior를 검증하는가?
- [ ] Invariant 검증이 포함되었는가?
- [ ] Assertion이 반드시 존재하고 의미 있는가?
- [ ] AI 특유의 불필요한 assertion이 없는가?
- [ ] 사람이 읽고 이해 가능한가?
- [ ] 반려 기준에 해당하는 항목이 없는가?

---

# 종합 요약 체크리스트 (PR 리뷰 시 최종 확인)

- [ ] 핵심 Invariant와 Observable Behavior를 명확히 테스트했는가?
- [ ] 구현 세부사항이 아닌 관찰 가능한 결과를 검증했는가?
- [ ] 테스트 이름이 명확하고 일관된가? (행동 중심, 모호한 동사 없음)
- [ ] Mock을 최소화하고 Fake/실제 구현을 우선 사용했는가?
- [ ] Test Double 허용 기준(3가지) 중 하나 이상에 해당하는가?
- [ ] 테스트 피라미드 구조를 역전시키지 않았는가?
- [ ] Property-based testing을 적절히 활용했는가?
- [ ] Boundary, Edge, Failure 케이스를 모두 포함했는가?
- [ ] Regression Test가 포함되었는가?
- [ ] Complexity 문제를 고려했는가?
- [ ] Flaky 요소가 완전히 제거되었는가?
- [ ] Flaky 테스트에 격리 프로세스(이슈+담당자+기한+원인가설)를 적용했는가?
- [ ] Parameterized Test를 적절히 사용했는가?
- [ ] Floating point 비교 규칙을 준수했는가?
- [ ] 테스트 가독성이 충분한가?
- [ ] AI 생성 테스트를 사람이 검토했는가?
- [ ] PR Red Flags 반려 기준에 해당하는 항목이 없는가?
- [ ] C++/Java 프레임워크 컨벤션을 준수했는가?
