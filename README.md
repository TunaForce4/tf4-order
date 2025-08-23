# Order-Service

## 5. 라이브러리 사용 이유

> Querydsl

- JPQL의 컴파일 시점 오류 확인 불가
- 쿼리 가독성과 타입 안정성을 보장하여 유지보수 용이

## 7. 브랜치 및 디렉토리 구조

> 디렉토리 구조

<details>
<summary>Order</summary>

<img src="https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdna%2FoNJ0s%2FbtsP0kVtl0e%2FAAAAAAAAAAAAAAAAAAAAADM3LCZeBIdOc5jyhbO8V6yYYZd-1D2YjPrSTM_QLyl_%2Fimg.png%3Fcredential%3DyqXZFxpELC7KVnFOS48ylbz2pIh7yKj8%26expires%3D1756652399%26allow_ip%3D%26allow_referer%3D%26signature%3DyXgSlq6KJHf4wJ9asK8sHiN33ck%253D" width="300" height="500" />

</details>

<details>
<summary>Product</summary>

<img src="https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdna%2Fbt3DBS%2FbtsPYNYDHku%2FAAAAAAAAAAAAAAAAAAAAAME3BC2QgCGwb4NFkaS2lKaoVr_Dktz8DoaAWSidXjqG%2Fimg.png%3Fcredential%3DyqXZFxpELC7KVnFOS48ylbz2pIh7yKj8%26expires%3D1756652399%26allow_ip%3D%26allow_referer%3D%26signature%3DYZZyl%252FtdBHZ7T%252FRXNeWgIFzOqUk%253D" width="300" height="500" />

</details>

## 8. 주요 기능

### 8) Order

| 기능                   | 메서드    | 엔드포인트                         | Query Params     | status |
|----------------------|--------|-------------------------------|------------------|--------|
| 주문 생성                | POST   | /orders                       |                  | 201    |
| 주문 단건 조회             | GET    | /orders/{orderId}             |                  | 200    |
| 허브 주문 목록 조회 (페이지네이션) | GET    | /orders/hubs/{hubId}          | page, size, sort | 200    |
| 업체 주문 목록 조회 (페이지네이션) | GET    | /orders/companies/{companyId} | page, size, sort | 200    |
| 주문 수정                | PATCH  | /orders/{orderId}             |                  | 204    |
| 주문 취소                | PATCH  | /orders/{orderId}/cancel      |                  | 204    |
| 주문 삭제                | DELETE | /orders/{orderId}             |                  | 200    |

### 9) Product

| 기능                      | 메서드    | 엔드포인트                           | Query Params                  | status |
|-------------------------|--------|---------------------------------|-------------------------------|--------|
| 상품 생성                   | POST   | /products                       |                               | 201    |
| 상품 단건 조회                | GET    | /products/{productId}           |                               | 200    |
| 상품 전체 목록 조회 (페이지네이션)    | GET    | /products                       | page, size, sort, productName | 200    |
| 허브 등록 상품 목록 조회 (페이지네이션) | GET    | /products/hubs/{hubId}          | page, size, sort, productName | 200    |
| 업체 등록 상품 목록 조회 (페이지네이션) | GET    | /products/companies/{companyId} | page, size, sort, productName | 200    |
| 상품 수정                   | PATCH  | /products                       |                               | 204    |
| 상품 삭제                   | DELETE | /products                       |                               | 200    |

## 9. 상세 업무

### Order

- 주문 생성
    - **POST** `/orders`
    - 권한 검증 → 주문 데이터 생성(PENDING) → 재고 차감(PREPARED) → 결제 처리(PAID, 가정) → 배송 생성(READY_FOR_SHIPMENT)
    - 권한
        - 허브 담당자: 주문하려는 상품의 담당 업체가 본인 허브 소속 업체이면 주문 불가능
        - 배송 담당자: 주문하려는 상품의 담당 업체가 본인 소속 허브의 소속 업체이면 주문 불가능
        - 업체 담당자: 주문하려는 상품의 담당 업체가 본인 업체이면 주문 불가능

- 주문 단건 조회
    - **GET** `/orders/{orderId}`
    - 조회 → 권한 검증 → 응답
    - 권한
        - 허브 담당자: 본인 허브 소속 업체의 주문 내역만 조회 가능
        - 배송 담당자/업체 담당자: 본인의 주문 내역만 조회 가능 (createdBy)

- 허브 주문 목록 조회 (페이지네이션)
    - **GET** `/orders/hubs/{hubId}?page=&size=&sort=`
    - 권한 검증 → 조회
    - 권한
        - 허브 담당자: 본인 허브의 경우에만 주문 내역 조회 가능
        - 배송 담당자/업체 담당자: 조회 불가능

- 업체 주문 목록 조회 (페이지네이션)
    - **GET** `/orders/companies/{companyId}?page=&size=&sort=`
    - 권한 검증 → 조회
    - 권한
        - 허브 담당자: 본인 허브의 경우에만 주문 내역 조회 가능
        - 배송 담당자/업체 담당자: 조회 불가능

- 주문 수정/삭제
    - **PATCH** `/orders/{orderId}`
    - **DELETE** `/orders/{orderId}`
    - 권한 검증 → 수정/삭제
    - 권한
        - 허브 담당자: 본인 허브 소속 업체의 주문만 수정/삭제 가능
        - 배송 담당자/업체 담당자: 수정/삭제 불가능

- 유저 권한별 각 기능 유효성 검증
    <details>
        <summary>
            주문 생성
        </summary>

  - POST /orders
  - 권한 검증 -> 주문 데이터 생성(PENDING) -> 재고 차감(PREPARED) -> 결제 처리(PAID, 가정) -> 배송 생성(READY_FOR_SHIPMENT) 
  - 권한
    - 허브 담당자: 주문하려는 상품의 담당 업체가 본인 허브 소속 업체이면 주문 불가능
    - 배송 담당자: 주문하려는 상품의 담당 업체가 본인 소속 허브의 소속 업체이면 주문 불가능
    - 업체 담당자: 주문하려는 상품의 담당 업체가 본인 업체이면 주문 불가능

  ![주문_생성](https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdna%2FxPINg%2FbtsP4XSCxSS%2FAAAAAAAAAAAAAAAAAAAAAG3ESMtMPZZUjmi_ZdQ9NwREYadL5FTRktzr09W8QqI3%2Fimg.png%3Fcredential%3DyqXZFxpELC7KVnFOS48ylbz2pIh7yKj8%26expires%3D1756652399%26allow_ip%3D%26allow_referer%3D%26signature%3D5RQHDbC5tbqrJP99tSOOM13DldA%253D)

    </details>
    <details>
        <summary>
            주문 단건 조회
        </summary>

  - GET /orders/{orderId}
  - 조회 -> 권한 검증 -> 응답
  - 권한 검증
    - 허브 담당자: 본인 허브 소속 업체의 주문 내역만 조회 가능
    - 배송 담당자/업체 담당자: 본인의 주문 내역만 조회 가능(createdBy)

  ![주문_단건_조회](https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdna%2FYUyMM%2FbtsP42M6yUs%2FAAAAAAAAAAAAAAAAAAAAAFtvhHyTD1SoIVRVKkI7FYjbjipnyLIGJaoOQAkHFwio%2Fimg.png%3Fcredential%3DyqXZFxpELC7KVnFOS48ylbz2pIh7yKj8%26expires%3D1756652399%26allow_ip%3D%26allow_referer%3D%26signature%3DE1uE4nSANqi9pkdUro5zhup7jCQ%253D)

    </details>
    <details>
        <summary>
            허브 주문 목록 조회(페이지네이션)
        </summary>

  - GET /orders/hubs/{hubId}?page=&size=&sort=
  - 권한 검증 -> 조회
  - 권한 검증
    - 허브 담당자: 본인 허브의 경우에만 주문 내역 조회 가능
    - 배송 담당자/업체 담당자: 조회 불가능

  ![허브_주문_목록_조회](https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdna%2FSx1JW%2FbtsP3zygfB5%2FAAAAAAAAAAAAAAAAAAAAAFd-X_oZDXYSbyb7TEpAEcYIKXNHreH9YjDFQJatRZNV%2Fimg.png%3Fcredential%3DyqXZFxpELC7KVnFOS48ylbz2pIh7yKj8%26expires%3D1756652399%26allow_ip%3D%26allow_referer%3D%26signature%3Dm31hYum7%252BBoLusHd13TL145wdIw%253D)

    </details>
    <details>
        <summary>
            업체 주문 목록 조회(페이지네이션)
        </summary>

  - GET /orders/companies/{companyId}?page=&size=&sort=
  - 권한 검증 -> 조회
  - 권한 검증
      - 허브 담당자: 본인 허브의 경우에만 주문 내역 조회 가능
      - 배송 담당자/업체 담당자: 조회 불가능 

  ![업체_주문_목록_조회](https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdna%2FcXccmB%2FbtsP4jaNFQz%2FAAAAAAAAAAAAAAAAAAAAALMtrwH1BsyE6SIlsnGzyL9IOusKKlfQYwE_O5HeyKTn%2Fimg.png%3Fcredential%3DyqXZFxpELC7KVnFOS48ylbz2pIh7yKj8%26expires%3D1756652399%26allow_ip%3D%26allow_referer%3D%26signature%3DyY6pur%252B7O%252B%252BshwyRWk90l9aPVXQ%253D)

    </details>
    <details>
        <summary>
            주문 수정/삭제
        </summary>

  - PATCH /orders/{orderId}
  - DELETE /orders/{orderId}
  - 권한 검증 -> 수정/삭제
  - 권한 검증
    - 허브 담당자: 본인 허브 소속 업체의 주문만 수정/삭제 가능
    - 배송 담당자/업체 담당자: 수정/삭제 불가능

  ![주문_수정](https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdna%2FbN4LQE%2FbtsP4JAhXSG%2FAAAAAAAAAAAAAAAAAAAAAMUSR8Er9AvpScmUnCaOTKrMCOvRfQwT7C4hlsTWR3kL%2Fimg.png%3Fcredential%3DyqXZFxpELC7KVnFOS48ylbz2pIh7yKj8%26expires%3D1756652399%26allow_ip%3D%26allow_referer%3D%26signature%3D39sfLxpMiYSjgpYO%252FE%252BVPnrF4%252Fo%253D)

    </details>

### Product

- 상품 생성
    - **POST** `/products`
    - 권한 검증 → 상품 생성
    - 권한
        - 허브 담당자: 등록하려는 상품의 담당 업체가 본인 허브 소속 업체이면 상품 등록 가능
        - 업체 담당자: 등록하려는 상품의 담당 업체가 본인 업체이면 상품 등록 가능

- 상품 단건 조회
    - **GET** `/products/{productId}`
    - 조회 → 권한 검증 → 응답
    - 권한
        - 허브 담당자: 조회하려는 상품의 담당 업체가 본인 허브 소속 업체이면 조회 가능
        - 업체 담당자: 조회하려는 상품의 담당 업체가 본인 업체이면 조회 가능
        - 배송 담당자: 조회 불가능

- 주문용 상품 전체 조회 (페이지네이션)
    - **GET** `/products?page=&size=&sort=&productName=`
    - 조회 → 응답

- 허브 등록 상품 목록 조회 (페이지네이션)
    - **GET** `/products/hubs/{hubId}?page=&size=&sort=&productName=`
    - 권한 검증 → 조회 → 응답
    - 권한
        - 허브 담당자: 본인 허브 소속 업체들의 등록 상품 목록 조회 가능
        - 배송 담당자/업체 담당자: 조회 불가능

- 업체 등록 상품 목록 조회 (페이지네이션)
    - **GET** `/products/companies/{companyId}?page=&size=&sort=&productName=`
    - 권한 검증 → 조회 → 응답
    - 권한
        - 허브 담당자: 본인 허브 소속 업체의 등록 상품 목록 조회 가능
        - 배송 담당자: 조회 불가능
        - 업체 담당자: 본인 업체의 등록 상품 목록 조회 가능

- 상품 수정
    - **PATCH** `/products/{productId}`
    - 권한 검증 → 수정
    - 권한
        - 허브 담당자: 본인 허브 소속 업체의 등록 상품 수정 가능
        - 업체 담당자: 본인 업체의 등록 상품 수정 가능
        - 배송 담당자: 수정 불가능

- 상품 삭제
    - **DELETE** `/products/{productId}`
    - 권한 검증 → 삭제
    - 권한
        - 허브 담당자: 본인 허브 소속 업체의 등록 상품 삭제 가능
        - 배송 담당자/업체 담당자: 삭제 불가능

- 유저 권한별 각 기능 유효성 검증
    <details>
        <summary>
            상품 생성
        </summary>

    - POST /products
    - 권한 검증 -> 상품 생성
    - 권한
        - 허브 담당자: 등록하려는 상품의 담당 업체가 본인 허브 소속 업체이면 상품 등록 가능
        - 업체 담당자: 등록하려는 상품의 담당 업체가 본인 업체이면 상품 등록 가능

  ![상품_생성](https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdna%2FbkOHf3%2FbtsP418xBfU%2FAAAAAAAAAAAAAAAAAAAAANTAYV6Flmf0aYQFKiT4bxZ1rYOAtrKdJiaVmSiT-Djf%2Fimg.png%3Fcredential%3DyqXZFxpELC7KVnFOS48ylbz2pIh7yKj8%26expires%3D1756652399%26allow_ip%3D%26allow_referer%3D%26signature%3DHfO4qIDI98YUiZOjXkxT9a%252FovSc%253D)

    </details>
    <details>
        <summary>
            상품 단건 조회
        </summary>

    - GET /products/{productId}
    - 조회 -> 권한 검증 -> 응답
    - 권한
      - 허브 담당자: 조회하려는 상품의 담당 업체가 본인 허브 소속 업체이면 조회 가능
      - 업체 담당자: 조회하려는 상품의 담당 업체가 본인 업체이면 조회 가능
      - 배송 담당자: 조회 불가능

  ![상품_단건_조회](https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdna%2FcENDh5%2FbtsP0TdqBr5%2FAAAAAAAAAAAAAAAAAAAAANf3fNevha4wHkfxJJ67djEjfy5lv1rKUe3KXfPKcgpX%2Fimg.png%3Fcredential%3DyqXZFxpELC7KVnFOS48ylbz2pIh7yKj8%26expires%3D1756652399%26allow_ip%3D%26allow_referer%3D%26signature%3D3U%252FQRnYrfBsrAEKrd0mYSjTZ5bU%253D)

    </details>
    <details>
        <summary>
            주문용 상품 전체 조회(페이지네이션)
        </summary>

    - GET /products?page=&size=&sort=&productName=
    - 조회 -> 응답

  ![상품_단건_조회](https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdna%2FcENDh5%2FbtsP0TdqBr5%2FAAAAAAAAAAAAAAAAAAAAANf3fNevha4wHkfxJJ67djEjfy5lv1rKUe3KXfPKcgpX%2Fimg.png%3Fcredential%3DyqXZFxpELC7KVnFOS48ylbz2pIh7yKj8%26expires%3D1756652399%26allow_ip%3D%26allow_referer%3D%26signature%3D3U%252FQRnYrfBsrAEKrd0mYSjTZ5bU%253D)

    </details>
    <details>
        <summary>
            허브 등록 상품 목록 조회(페이지네이션)
        </summary>

    - GET /products/hubs/{hubId}?page=&size=&sort=&productName=
    - 권한 검증 -> 조회 -> 응답
    - 권한
      - 허브 담당자: 본인 허브 소속 업체들의 등록 상품 목록 조회 가능
      - 배송 담당자/업체 담당자: 조회 불가능

  ![허브_등록_상품_목록_조회](https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdna%2Fb4gF5b%2FbtsP4e1BM6F%2FAAAAAAAAAAAAAAAAAAAAAK6eFEKK3E7u75yOHEyw7wyeLkgoXhoURZRjijhMSXAu%2Fimg.png%3Fcredential%3DyqXZFxpELC7KVnFOS48ylbz2pIh7yKj8%26expires%3D1756652399%26allow_ip%3D%26allow_referer%3D%26signature%3DiF7Tes%252FSHgq6KK4yk8skVfxcetU%253D)

    </details>
    <details>
        <summary>
            업체 등록 상품 목록 조회(페이지네이션)
        </summary>

    - GET /products/companies/{companyId}?page=&size=&sort=&productName=
    - 권한 검증 -> 조회 -> 응답
    - 권한
      - 허브 담당자: 본인 허브 소속 업체의 등록 상품 목록 조회 가능
      - 배송 담당자: 조회 불가능
      - 업체 담당자: 본인 업체의 등록 상품 목록 조회 가능

  ![업체_등록_상품_목록_조회](https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdna%2FcBTWKX%2FbtsP4qnocyX%2FAAAAAAAAAAAAAAAAAAAAAB0R89YUWV3XmIA38WrNvcG-_dHulc9O1Eb-FpLAxjyY%2Fimg.png%3Fcredential%3DyqXZFxpELC7KVnFOS48ylbz2pIh7yKj8%26expires%3D1756652399%26allow_ip%3D%26allow_referer%3D%26signature%3DGajD4ta0IdFS47DK2W%252BrkfmgUK4%253D)

    </details>
    <details>
        <summary>
            상품 수정
        </summary>

    - PATCH /products/{productId}
    - 권한 검증 -> 수정
    - 권한
      - 허브 담당자: 본인 허브 소속 업체의 등록 상품에 대해서 수정 가능
      - 배송 담당자: 수정 불가능
      - 업체 담당자: 본인 업체의 등록 상품에 대해서 수정 가능

  ![상품_수정](https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdna%2FxCLnK%2FbtsP4624ifa%2FAAAAAAAAAAAAAAAAAAAAADOQQIn41TP2pwZ0BqZHCZWjLL1KsOUAGAr106eHNrTi%2Fimg.png%3Fcredential%3DyqXZFxpELC7KVnFOS48ylbz2pIh7yKj8%26expires%3D1756652399%26allow_ip%3D%26allow_referer%3D%26signature%3DmT3cGgJRQjY1AKMHSh2Xe6G8poI%253D)

    </details>
    <details>
        <summary>
            상품 삭제
        </summary>

    - DELETE /products/{productId}
    - 권한 검증 -> 삭제
    - 권한
      - 허브 담당자: 본인 허브 소속 업체의 등록 상품에 대해서 삭제 가능
      - 배송 담당자/업체 담당자: 삭제 불가능

  ![상품_삭제](https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdna%2Fb11bVy%2FbtsP4iQtGIB%2FAAAAAAAAAAAAAAAAAAAAAI8faBKIc_q6REmCQGIDYSxiT_pvo8IPVVOMuqHk75wi%2Fimg.png%3Fcredential%3DyqXZFxpELC7KVnFOS48ylbz2pIh7yKj8%26expires%3D1756652399%26allow_ip%3D%26allow_referer%3D%26signature%3DF%252FQTJXHI3PKAvxOUBfWsQ8drl0A%253D)

    </details>

### 공통

- DTO의 `record` 타입 사용
    - 클라이언트의 요청에 대한 불변성과 안정성을 명시


- `Pageable`의 `sort` 쿼리 - 정렬 조건 타입 안정화
    - 정해진 정렬 조건만 허용 - 예기치 못한 쿼리 또는 에러 발생 방지
    - 동일한 `property` 정렬 시 예외 발생
    - `SortType` ENUM
      ```
      @Getter
      @RequiredArgsConstructor
      public enum SortType {
      
          CREATED_ASC("createdAt", Sort.Direction.ASC),
          CREATED_DESC("createdAt", Sort.Direction.DESC),
          UPDATED_ASC("updatedAt", Sort.Direction.ASC),
          UPDATED_DESC("updatedAt", Sort.Direction.DESC),
          PRICE_ASC("price", Sort.Direction.ASC),
          PRICE_DESC("price", Sort.Direction.DESC),
          ;
      
          private final String value;
          private final Sort.Direction direction;
      
          public static void validate(Sort sort) {
              Set<String> check = new HashSet<>();

              for (Sort.Order order : sort) {
                  if (!match(order)) {
                      throw new CustomRuntimeException(ProductException.UNSUPPORTED_SORT_TYPE);
                  }

                  if (!check.add(order.getProperty().toLowerCase())) {
                      throw new CustomRuntimeException(ProductException.DUPLICATED_SORT_TYPE);
                  }
              }
          }
      
          private static boolean match(Sort.Order order) {
              return Arrays.stream(SortType.values())
                      .anyMatch(sortType -> sortType.value.equalsIgnoreCase(order.getProperty()) &&
                         sortType.getDirection().equals(order.getDirection()));
          }
      }
      ```

- `Querydsl`의 `getOrderSpecifier()`을 통한 동적 정렬 조건 쿼리 구현
    - 클라이언트가 여러 개의 `sort` 쿼리를 넘기도록 하여 `.orderBy()`에 적용
      ```
      private OrderSpecifier<?>[] getOrderSpecifiers(Sort sort) {
        List<OrderSpecifier<?>> orderSpecifiers = new ArrayList<>();
    
        for (Sort.Order sortOrder : sort) {
            Order order = sortOrder.isAscending() ? Order.ASC : Order.DESC; // sort 쿼리 내 정렬 방향을 Order 객체로 파싱 
            PathBuilder<Product> pathBuilder
                    = new PathBuilder<>(product.getType(), product.getMetadata()); // sort 쿼리 내 정렬 값을 QClass 내 필드값에 해당하는 값으로 매핑  
    
            orderSpecifiers.add(new OrderSpecifier<>(order, pathBuilder.getString(sortOrder.getProperty())));
        }
    
        return orderSpecifiers.toArray(new OrderSpecifier[0]);
      }
      ```

## 11. 트러블 슈팅

<details>
<summary> OpenFeign Invalid HTTP method: PATCH </summary>

<div>

1. 문제 상황 <br />
   ![openfeign_trouble_1](https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdna%2FsenN2%2FbtsP228uae0%2FAAAAAAAAAAAAAAAAAAAAAFPM1kh3cVJlaIyepRMGq-26xrPuLBlypU56BVkH8Kex%2Fimg.png%3Fcredential%3DyqXZFxpELC7KVnFOS48ylbz2pIh7yKj8%26expires%3D1756652399%26allow_ip%3D%26allow_referer%3D%26signature%3D1Adhf2iRqBIxWFno4%252BOSC6ThSec%253D)
   ![openfeign_trouble_2](https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdna%2FcsXI7X%2FbtsP11WPACl%2FAAAAAAAAAAAAAAAAAAAAAFnLL7tOaohP5ePtMOrW6aTPrfonpf0_FuxwVNiufRCa%2Fimg.png%3Fcredential%3DyqXZFxpELC7KVnFOS48ylbz2pIh7yKj8%26expires%3D1756652399%26allow_ip%3D%26allow_referer%3D%26signature%3DkUVqHuGmZoJ2BENQnpyiIkVzt9Y%253D)

- `Order`서비스에서 `Product`서비스로 주문 상품 수량 수정이나 주문 취소시 차감했던 상품의 재고를 다시 변경하기 위해 `PATCH` 메소드 사용
- `OpenFeign`은 HTTP 요청을 추상화하여 메소드 호출로 REST API 호출 가능하도록 지원해주는데 HTTP 기본 구현체가 추가 의존성 없이 작동하도록 설계됨
- `HttpURLConnection`은 별도 라이브러리 의존 없이 사용 가능하므로 `OpenFeign`에서 사용
- `PATCH`는 비교적 최신 기능이라 `HttpURLConnection`에서 지원이 안되는 것

2. 해결방안 <br />

- 외부 HTTP 클라이언트 라이브러리를 사용하여 해결(커스텀)
    - ApachHttpClient, OkHttpClient
- 하지만 각 서비스 간 내부 통신이므로 Restful 규약을 준수할 필요는 없다고 생각함

3. 최종 결정

- `PATCH` -> `POST` 변경

</div>
</details>

## 12. 회고

### 최우탁

- 권한별 기능 설계 및 구현에 시간을 너무 사용하여 `OpenFeign` 통신 장애 대응 로직을 충분히 구현하지 못해 아쉽다.
- 각 기능의 최적화나 고도화를 진행하지 못한 점도 개선할 필요가 있다.
- 다음 프로젝트에서는 설계 단계에서 서비스의 전체적인 도메인에 대한 이해를 충분한 회의나 지식 공유등을 통해 정리하고 가는 것이 좋을 것 같다.

