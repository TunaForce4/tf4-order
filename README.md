### Order-Service

### 5. 라이브러리 사용 이유

- Querydsl
    - 유지보수
        - JPQL의 컴파일 시점 오류 확인 불가
        - 타입 안정성을 보장
        - 가독성

### 7. 브랜치 및 디렉토리 구조

> 디렉토리 구조

![directory_structure_order](https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdna%2FoNJ0s%2FbtsP0kVtl0e%2FAAAAAAAAAAAAAAAAAAAAADM3LCZeBIdOc5jyhbO8V6yYYZd-1D2YjPrSTM_QLyl_%2Fimg.png%3Fcredential%3DyqXZFxpELC7KVnFOS48ylbz2pIh7yKj8%26expires%3D1756652399%26allow_ip%3D%26allow_referer%3D%26signature%3DyXgSlq6KJHf4wJ9asK8sHiN33ck%253D)

### 8. 주요 기능

| 기능                   | 메서드    | 엔드포인트                         | Query Params     | status |
|----------------------|--------|-------------------------------|------------------|--------|
| 주문 생성                | POST   | /orders                       |                  | 201    |
| 주문 단건 조회             | GET    | /orders/{orderId}             |                  | 200    |
| 허브 주문 목록 조회 (페이지네이션) | GET    | /orders/hubs/{hubId}          | page, size, sort | 200    |
| 업체 주문 목록 조회 (페이지네이션) | GET    | /orders/companies/{companyId} | page, size, sort | 200    |
| 주문 수정                | PATCH  | /orders/{orderId}             |                  | 204    |
| 주문 취소                | PATCH  | /orders/{orderId}/cancel      |                  | 204    |
| 주문 삭제                | DELETE | /orders/{orderId}             |                  | 200    |

### 9. 상세 업무

- 유저 권한별 각 기능 유효성 검증
    <details>
        <summary>
            주문 생성
        </summary>

  ![주문_생성](https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdna%2FbkOHf3%2FbtsP418xBfU%2FAAAAAAAAAAAAAAAAAAAAANTAYV6Flmf0aYQFKiT4bxZ1rYOAtrKdJiaVmSiT-Djf%2Fimg.png%3Fcredential%3DyqXZFxpELC7KVnFOS48ylbz2pIh7yKj8%26expires%3D1756652399%26allow_ip%3D%26allow_referer%3D%26signature%3DHfO4qIDI98YUiZOjXkxT9a%252FovSc%253D)

    </details>
    <details>
        <summary>
            주문 단건 조회
        </summary>

  ![주문_단건_조회](https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdna%2FcENDh5%2FbtsP0TdqBr5%2FAAAAAAAAAAAAAAAAAAAAANf3fNevha4wHkfxJJ67djEjfy5lv1rKUe3KXfPKcgpX%2Fimg.png%3Fcredential%3DyqXZFxpELC7KVnFOS48ylbz2pIh7yKj8%26expires%3D1756652399%26allow_ip%3D%26allow_referer%3D%26signature%3D3U%252FQRnYrfBsrAEKrd0mYSjTZ5bU%253D)

    </details>
    <details>
        <summary>
            허브 주문 목록 조회
        </summary>

  ![허브_주문_목록_조회](https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdna%2Fb4gF5b%2FbtsP4e1BM6F%2FAAAAAAAAAAAAAAAAAAAAAK6eFEKK3E7u75yOHEyw7wyeLkgoXhoURZRjijhMSXAu%2Fimg.png%3Fcredential%3DyqXZFxpELC7KVnFOS48ylbz2pIh7yKj8%26expires%3D1756652399%26allow_ip%3D%26allow_referer%3D%26signature%3DiF7Tes%252FSHgq6KK4yk8skVfxcetU%253D)

    </details>
    <details>
        <summary>
            업체 주문 목록 조회
        </summary>

  ![업체_주문_목록_조회](https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdna%2FcBTWKX%2FbtsP4qnocyX%2FAAAAAAAAAAAAAAAAAAAAAB0R89YUWV3XmIA38WrNvcG-_dHulc9O1Eb-FpLAxjyY%2Fimg.png%3Fcredential%3DyqXZFxpELC7KVnFOS48ylbz2pIh7yKj8%26expires%3D1756652399%26allow_ip%3D%26allow_referer%3D%26signature%3DGajD4ta0IdFS47DK2W%252BrkfmgUK4%253D)

    </details>
    <details>
        <summary>
            주문 수정
        </summary>

  ![주문_수정](https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdna%2FxCLnK%2FbtsP4624ifa%2FAAAAAAAAAAAAAAAAAAAAADOQQIn41TP2pwZ0BqZHCZWjLL1KsOUAGAr106eHNrTi%2Fimg.png%3Fcredential%3DyqXZFxpELC7KVnFOS48ylbz2pIh7yKj8%26expires%3D1756652399%26allow_ip%3D%26allow_referer%3D%26signature%3DmT3cGgJRQjY1AKMHSh2Xe6G8poI%253D)

    </details>
    <details>
        <summary>
            주문 삭제
        </summary>

  ![주문_삭제](https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdna%2Fb11bVy%2FbtsP4iQtGIB%2FAAAAAAAAAAAAAAAAAAAAAI8faBKIc_q6REmCQGIDYSxiT_pvo8IPVVOMuqHk75wi%2Fimg.png%3Fcredential%3DyqXZFxpELC7KVnFOS48ylbz2pIh7yKj8%26expires%3D1756652399%26allow_ip%3D%26allow_referer%3D%26signature%3DF%252FQTJXHI3PKAvxOUBfWsQ8drl0A%253D)

    </details>


- DTO의 `record` 타입 사용
  - 클라이언트의 요청에 대한 불변성과 안정성을 명시


- `Pageable`의 `sort` 쿼리 - 정렬 조건 타입 안정화
    - 정해진 정렬 조건만 허용 - 예기치 못한 쿼리 또는 에러 발생 방지
    - `SortType` ENUM
      ```
      @Getter
      @RequiredArgsConstructor
      public enum SortType {
      
          CREATED_ASC("createdAt", Sort.Direction.ASC),
          CREATED_DESC("createdAt", Sort.Direction.DESC),
          UPDATED_ASC("updatedAt", Sort.Direction.ASC),
          UPDATED_DESC("updatedAt", Sort.Direction.DESC),
          PRICE_ASC("orderPrice", Sort.Direction.ASC),
          PRICE_DESC("orderPrice", Sort.Direction.DESC),
          ;
      
          private final String value;
          private final Sort.Direction direction;
      
          public static void validate(Sort sort) {
              for (Sort.Order order : sort) {
                  boolean valid = Arrays.stream(SortType.values())
                          .anyMatch(sortType -> sortType.value.equalsIgnoreCase(order.getProperty()) &&
                                  sortType.getDirection().equals(order.getDirection()));
      
                  if (!valid) {
                      throw new CustomRuntimeException(OrderException.UNSUPPORTED_SORT_TYPE);
                  }
              }
          }
      }
      ```

- `Querydsl`의 `getOrderSpecifier()`을 통한 동적 정렬 조건 쿼리 구현
    - 클라이언트가 여러 개의 `sort` 쿼리를 넘기도록 하여 `.orderBy()`에 적용
      ```
      private OrderSpecifier<?>[] getOrderSpecifiers(Sort sort) {
        List<OrderSpecifier<?>> orderSpecifiers = new ArrayList<>();
    
        for (Sort.Order sortOrder : sort) {
            Order order = sortOrder.isAscending() ? Order.ASC : Order.DESC; // 정렬 방향 
            PathBuilder<com.tunaforce.order.entity.Order> pathBuilder
                    = new PathBuilder<>(QOrder.order.getType(), QOrder.order.getMetadata());
    
            orderSpecifiers.add(new OrderSpecifier<>(order, pathBuilder.getString(sortOrder.getProperty())));
        }
    
        return orderSpecifiers.toArray(new OrderSpecifier[0]);
      }
      ```
      
### 11. 트러블 슈팅 

- `OpenFeign`의 HTTP `PATCH` 메소드 미지원
  - 원인
    ![openfeign_trouble_1](https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdna%2FsenN2%2FbtsP228uae0%2FAAAAAAAAAAAAAAAAAAAAAFPM1kh3cVJlaIyepRMGq-26xrPuLBlypU56BVkH8Kex%2Fimg.png%3Fcredential%3DyqXZFxpELC7KVnFOS48ylbz2pIh7yKj8%26expires%3D1756652399%26allow_ip%3D%26allow_referer%3D%26signature%3D1Adhf2iRqBIxWFno4%252BOSC6ThSec%253D)
    ![openfeign_trouble_2](https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdna%2FcsXI7X%2FbtsP11WPACl%2FAAAAAAAAAAAAAAAAAAAAAFnLL7tOaohP5ePtMOrW6aTPrfonpf0_FuxwVNiufRCa%2Fimg.png%3Fcredential%3DyqXZFxpELC7KVnFOS48ylbz2pIh7yKj8%26expires%3D1756652399%26allow_ip%3D%26allow_referer%3D%26signature%3DkUVqHuGmZoJ2BENQnpyiIkVzt9Y%253D)
    - `OpenFeign`은 HTTP 요청을 추상화하여 메소드 호출로 REST API 호출 가능하도록 지원해주는데 HTTP 기본 구현체가 추가 의존성 없이 작동하도록 설계됨
    - `HttpURLConnection`은 별도 라이브러리 의존 없이 사용 가능하므로 `OpenFeign`에서 사용
    - `PATCH`는 비교적 최신 기능이라 지원이 안되는 것  
  - 해결 방안
    - 외부 HTTP 클라이언트 라이브러리를 사용하여 해결(커스텀)
      - ApachHttpClient, OkHttpClient
    - 하지만 각 서비스 간 내부 통신이므로 Restful 규약을 준수할 필요는 없다고 생각함
  - 최종 결정
    - `PATCH` -> `POST` 변경