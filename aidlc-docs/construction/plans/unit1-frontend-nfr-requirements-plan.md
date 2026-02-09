# NFR Requirements Plan - Unit 1 (Frontend)

## Context

**Unit**: Unit 1 - Frontend (Customer + Admin UI)
**Tech Stack**: React, TypeScript, Zustand, Axios

---

## NFR Questions

### Performance

#### Question 1
페이지 로딩 시간 목표는?

A) 1초 이내
B) 2초 이내
C) 3초 이내
D) 특별한 요구사항 없음

[Answer]: A

#### Question 2
SSE 연결 재연결 최대 대기 시간은?

A) 10초
B) 30초 (현재 설계)
C) 60초
D) Other

[Answer]: B

### Scalability

#### Question 3
동시 접속 고객 수 예상은?

A) 10명 이하
B) 10-50명
C) 50-100명
D) Other

[Answer]: A

### Security

#### Question 4
HTTPS 사용 여부는?

A) 필수 (프로덕션)
B) 선택 (개발 환경만 HTTP)
C) 불필요

[Answer]: B

### Tech Stack

#### Question 5
CSS 프레임워크는?

A) Tailwind CSS
B) CSS Modules
C) Styled Components
D) Material-UI

[Answer]: A

#### Question 6
상태 관리 persist 플러그인 사용?

A) Yes - zustand/middleware persist
B) No - 수동 LocalStorage/SessionStorage

[Answer]: A

---

## Artifacts to Generate

- [ ] **nfr-requirements.md**: NFR 요구사항
- [ ] **tech-stack-decisions.md**: 기술 스택 결정

---

## Instructions

답변 완료 후 알려주세요.
