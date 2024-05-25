# 서버 CPU 사용률 모니터링 시스템

<br>

## 개요

이 프로젝트는 서버의 CPU 사용률 데이터를 분 단위로 수집하여 데이터베이스에 저장하고, API를 통해 조회할 수 있는 시스템입니다. 제공된 API는 프론트엔드 화면에서 기간별 분, 시, 일 단위 그래프로 표현될 수 있습니다.

<br>

## 프로젝트 설정 및 실행 방법

#### 1. 프로젝트 설정
    Spring Boot 2.7.2
#####
    Java 17
#####
    Gradle
#####
    JPA
#####
    DB 환경 분리
    Prod             : Maria DB
    Local (Dev, Test): H2 DB (In-Memory)

#### 2. 프로젝트 실행 방법
    Maria DB 로컬 환경 접속
    username : root
    password : root
    CREATE DATABASE monitoring;
#####
    Spring Boot App으로 프로젝트 실행
#####
    Swagger API 문서 URL : http://localhost:8080/swagger-ui/index.html#/
#####    
    H2 DB (In-Memory) Console URL : http://localhost:8080/h2-console/
    username : sa
    password :

#### 3. 프로젝트 테스트 방법
    JUnit Test로 프로젝트 실행

![image](https://github.com/BeYoungWu/solo/assets/116334811/67daa35e-f128-4d06-901c-9c3231560d5d)
<br>(테스트 성공 화면)

<br>

## API 설명

#### 1. CPU 사용률 수집 및 저장 (1분 단위)
    POST
#####
    Spring Boot Actuator를 통해 조회 가능한 metrics 정보들 중 CPU 사용률 데이터를 수집하여 1분마다 데이터베이스에 저장합니다.
#####
    CPU 사용률 데이터 metric 정보 조회 URL : http://127.0.0.1:8080/actuator/metrics/system.cpu.usage

#### 2. CPU 사용률 분 단위 조회
    GET /api/cpu-usage/minute
#####
    지정한 시간 구간의 분 단위 CPU 사용률을 조회합니다.
#####
    입력값 예시
        - start : '2024-05-25 13:00:00'
        - end : '2024-05-25 13:10:00'

![image](https://github.com/BeYoungWu/solo/assets/116334811/75096038-2489-4134-84d1-fd5b41c70b73)
<br>(출력 결과 데이터)
#####
    출력값
        - id : 식별 번호
        - useTime : 2024-5-25 15:20:33.310264000
        - cpuUsage : CPU 사용률
#####
    최근 1주 이내의 데이터 제공

#### 3. CPU 사용률 시 단위 최소/최대/평균 조회
    GET /api/cpu-usage/hour
#####
    지정한 날짜의 시 단위 CPU 사용률 최소/최대/평균을 조회합니다.
#####
    입력값 예시
        - day : '2024-05-25'

![image](https://github.com/BeYoungWu/solo/assets/116334811/e7f969f5-71db-4890-aae8-d4b45103e4ac)
<br>(출력 결과 데이터)
#####
    출력값
        - '2024-05-25 15:00:00'
        - minUsage : 해당 시 최소 CPU 사용률
        - maxUsage : 해당 시 최대 CPU 사용률
        - avgUsage : 해당 시 평균 CPU 사용률
#####
    최근 3달 이내의 데이터 제공

#### 4. CPU 사용률 일 단위 최소/최대/평균 조회
    GET /api/cpu-usage/day
#####
    지정한 기간의 일 단위 CPU 사용률 최소/최대/평균을 조회합니다.
#####
    입력값 예시
        - start : '2024-05-24'
        - end : '2024-05-25'

![image](https://github.com/BeYoungWu/solo/assets/116334811/88d579b9-3f74-4b4e-8275-b330312abff5)
<br>(출력 결과 데이터)
#####
    출력값
        - '2024-05-25'
        - minUsage : 해당 시 최소 CPU 사용률
        - maxUsage : 해당 시 최대 CPU 사용률
        - avgUsage : 해당 시 평균 CPU 사용률
#####
    최근 1년 이내의 데이터 제공