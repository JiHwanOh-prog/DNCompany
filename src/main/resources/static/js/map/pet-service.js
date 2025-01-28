import {petServiceMapApi} from "./modules/mapApi.js";

// 1. 지도 생성
var mapContainer = document.getElementById('map'); // 지도를 표시할 div
var mapOption = {
    center: new kakao.maps.LatLng(37.5665, 126.9780), // 지도의 중심좌표 (서울 시청 기준)
    level: 5 // 지도의 확대 레벨
};

var map = new kakao.maps.Map(mapContainer, mapOption); // 지도를 생성합니다

// // 장소 검색 객체를 생성합니다
// var ps = new kakao.maps.services.Places();


// 마커를 담을 배열입니다
var markers = [];
var currentInfowindow = null;  // 열린 인포윈도우를 추적하는 변수
var currentCustomOverlay = null;

// 2. 지도 로드
function resizeMap() {
    var mapContainer = document.getElementById("map");
    mapContainer.style.width = "100%";
    mapContainer.style.height = "800px";
}

resizeMap();

function relayout() {
    // 지도를 표시하는 div 크기를 변경한 이후 지도가 정상적으로 표출되지 않을 수도 있습니다
    // 크기를 변경한 이후에는 반드시  map.relayout 함수를 호출해야 합니다
    // window의 resize 이벤트에 의한 크기변경은 map.relayout 함수가 자동으로 호출됩니다
    map.relayout();
}

relayout();

// 3. 페이지 처리 함수 불러오기
// pagination 데이터 변수 정의
var pagination = {
    current: 1, // 현재 페이지 번호
    perPage: 10, // 한 페이지당 가져올 데이터 개수
    last: 1, // 마지막 페이지 (초기화는 1로 설정)
    gotoPage: function(page) {
        this.current = page;
        fetchPlaces(page, this.perPage);
    }
};

// 페이지 번호를 표출합니다
displayPagination(pagination);

// 페이지 번호와 perPage 값을 기반으로 데이터를 업데이트하는 fetchPlaces 함수
function fetchPlaces(page, perPage) {
    // page와 perPage 값을 사용하여 데이터를 슬라이싱
    petServiceMapApi(function (mapApi) {
        var start = (page - 1) * perPage;
        var end = start + perPage;

        var paginatedData = mapApi.data.slice(start, end);

        pagination.last = Math.ceil(mapApi.data.length / perPage);

        displayPlaces(paginatedData); // 페이지에 해당하는 장소 표시
        displayPagination(pagination); // 페이지 번호 표시
    });
}

// 검색결과 목록 하단에 페이지번호를 표시하는 함수
function displayPagination(pagination) {
    const paginationEl = document.getElementById('pagination');
    const fragment = document.createDocumentFragment();
    const groupSize = 5; // 한 번에 표시할 페이지 수
    const currentGroup = Math.ceil(pagination.current / groupSize); // 현재 페이지 그룹 계산
    const startPage = (currentGroup - 1) * groupSize + 1; // 현재 그룹의 시작 페이지
    const endPage = Math.min(startPage + groupSize - 1, pagination.last); // 현재 그룹의 끝 페이지

    // 기존 페이지 번호 초기화
    while (paginationEl.hasChildNodes()) {
        paginationEl.removeChild(paginationEl.lastChild);
    }

    // 이전 그룹 버튼 추가
    if (startPage > 1) {
        const prevGroupButton = document.createElement('a');
        prevGroupButton.href = "#";
        prevGroupButton.innerHTML = "이전";
        prevGroupButton.onclick = function () {
            pagination.gotoPage(startPage - 1);
        };
        fragment.appendChild(prevGroupButton);
    }

    // 현재 그룹의 페이지 번호 추가
    for (let i = startPage; i <= endPage; i++) {
        const el = document.createElement('a');
        el.href = "#";
        el.innerHTML = i;

        if (i === pagination.current) {
            el.className = 'on';
        } else {
            el.onclick = (function (i) {
                return function () {
                    pagination.gotoPage(i); // 페이지 이동
                };
            })(i);
        }

        fragment.appendChild(el);
    }

    // 다음 그룹 버튼 추가
    if (endPage < pagination.last) {
        const nextGroupButton = document.createElement('a');
        nextGroupButton.href = "#";
        nextGroupButton.innerHTML = "다음";
        nextGroupButton.onclick = function () {
            pagination.gotoPage(endPage + 1);
        };
        fragment.appendChild(nextGroupButton);
    }

    // 추가된 요소를 DOM에 적용
    paginationEl.appendChild(fragment);
}

// 검색시 기존 마커는 제거하는 로직
function clearMarkers() {
    markers.forEach(function(marker) {
        marker.setMap(null);
    });
    markers = [];

    // 현재 열려 있는 커스텀 오버레이 닫기
    if (currentCustomOverlay) {
        currentCustomOverlay.setMap(null);
        currentCustomOverlay = null; // 열려 있는 오버레이 추적 변수 초기화
    }
}

function displayPlaces(places) {
    // 검색 전 기존 마커 지우는 로직
    clearMarkers();

    // 필터링된 장소 확인 (디버깅용)
    console.log("Displaying Places:", places);

    // JSON 데이터를 기반으로 마커를 생성하고 지도에 표시
    const markerMap = new Map();
    places.forEach(function (location) {
        const markerPosition = new kakao.maps.LatLng(location.lat, location.lng);
        const markerKey = `${location.lat},${location.lng}`; // 고유 키 생성 (lat,lng 조합)

        const marker = new kakao.maps.Marker({
            position: markerPosition,
            map: map,
            title: location.store_name
        });

        // Map 객체에 장소와 마커를 매핑
        markerMap.set(markerKey, marker);

        // 마커에 클릭 이벤트를 등록하여 인포윈도우를 표시
        kakao.maps.event.addListener(marker, 'click', function () {

            // 기존 커스텀 오버레이가 열려 있으면 닫기
            if (currentCustomOverlay) currentCustomOverlay.setMap(null);
            const customContent = document.createElement("div");
            customContent.className = "wrap";
            customContent.innerHTML = `
                <div class="wrap">
                    <div class="info-custom">
                        <div class="title"> ${location.store_name}
                            <div class="close" onclick="closeOverlay()" title="닫기"></div>
                        </div>
                        <div class="body">
                            <div class="desc">
                                <div class="ellipsis">주소 : ${location.address_road_name}</div>
                                <div class="jibun ellipsis">지번 : ${location.address_jibun_name}</div>
                                <span class="phone">${location.phone_number || "전화번호 없음"} |</span>
                                <span class="pet-info ${location.pets_allowed_info === 'Y' ? 'allowed' : 'not-allowed'}">
                                ${location.pets_allowed_info === 'Y' ? "반려동물 출입 가능" : "반려동물 출입 불가능"}
                                </span>
                                <div>
                                    <a href="${location.homepage_url !== '정보없음' ? location.homepage_url : '#'}" 
                                       target="${location.homepage_url !== '정보없음' ? '_blank' : '_self'}" 
                                       class="link"
                                       onclick="${location.homepage_url === '정보없음' ? 'alert(&quot;해당 장소는 홈페이지 제공이 되지 않습니다.&quot;); return false;' : ''}">
                                       홈페이지
                                    </a>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>`;
            // 닫기 버튼에 클릭 이벤트 추가
            const closeBtn = customContent.querySelector(".close");
            closeBtn.addEventListener("click", function () {
                overlay.setMap(null);
                currentCustomOverlay = null; // 현재 열려 있는 오버레이 추적 변수 초기화
            });

            // 마커 위에 커스텀오버레이를 표시합니다
            // 마커를 중심으로 커스텀 오버레이를 표시하기위해 CSS를 이용해 위치를 설정했습니다
            const overlay = new kakao.maps.CustomOverlay({
                content: customContent,
                map: map,
                position: markerPosition
            });

            currentCustomOverlay = overlay; // 열린 인포윈도우 추적
            overlay.setMap(map); // 마커를 클릭했을 때 커스텀 오버레이를 표시합니다
            map.setCenter(markerPosition); // 마커 클릭 시 해당 위치로 지도 중심 이동
        });

        // 생성된 마커를 배열에 추가
        markers.push(marker);
    });
    // 장소 리스트 업데이트 함수 호출
    updatePlacesList(places, markerMap);
}

function updatePlacesList(places, markerMap) {
    const placesList = document.getElementById('placesList');
    placesList.innerHTML = ''; // 기존 리스트 초기화

    // , index
    places.forEach(function(place) {
        const listItem = document.createElement('li');
        const markerKey = `${place.lat},${place.lng}`; // 고유 키 생성

        listItem.textContent = place.store_name;
        listItem.className = 'item';
        listItem.innerHTML = `
                    <div class="info">
                        <h5>${place.store_name}</h5>
                        <span>도로명 주소 : ${place.address_road_name}</span>
                        <span>지번 : ${place.address_jibun_name}</span>
                        <span>${place.phone_number || '전화번호 없음'}</span>
                    </div>
                `;
        // 리스트 항목 클릭 이벤트 추가
        listItem.addEventListener("click", function () {
            const marker = markerMap.get(markerKey);
            if (marker) {
                kakao.maps.event.trigger(marker, "click"); // 마커의 클릭 이벤트를 트리거
            }
        });
        placesList.appendChild(listItem);
    });
}

// 4. callback으로 받은 Json 데이터를 mapApi에 저장
petServiceMapApi(function (mapApi) {
    // 초기 로드 시 모든 장소의 마커를 표시
    displayPlaces(mapApi.data);
    pagination.current = 1; // 현재 페이지 초기화
    fetchPlaces(pagination.current, pagination.perPage);

    function searchPlaces(keyword) {
        if (!keyword || keyword.trim() === "") {
            alert("검색어를 입력해주세요.");
            return;
        }
        keyword = keyword.trim();

        if (!mapApi || !Array.isArray(mapApi.data)) {
            console.error("Invalid mapApi data");
            return;
        }
        console.log("mapApi.data before filtering:", mapApi.data);

        // mapApi.data에서 키워드로 필터링
        var filteredData = mapApi.data.filter(function (place) {
            if (!place) {
                console.warn("Null or undefined place encountered:", place);
                return false;
            }
            if (!place.store_name || !place.address_road_name) {
                console.warn("Missing fields in place object:", place);
                return false;
            }
            return (
                place.store_name.includes(keyword) ||
                place.address_road_name.includes(keyword) ||
                place.category_business_store_name.includes(keyword)
            );
        });

        // 필터링된 데이터 확인 (디버깅용)
        console.log("Filtered Data:", filteredData);

        if (filteredData.length === 0) {
            alert("검색 결과가 없습니다.");

            // 지도에서 모든 마커 제거 및 인포윈도우 닫기
            clearMarkers();
        } else {
            // 검색된 데이터로 pagination 갱신
            pagination.current = 1; // 검색 결과는 항상 첫 페이지로 시작
            pagination.last = Math.ceil(filteredData.length / pagination.perPage);

            // 검색된 데이터에 따라 페이징 처리된 장소 표시
            const start = (pagination.current - 1) * pagination.perPage;
            const end = start + pagination.perPage;
            const paginatedData = filteredData.slice(start, end);

            // 지도와 리스트 갱신
            displayPlaces(paginatedData);
            displayPagination(pagination);

            // 검색된 첫 번째 장소의 마커를 클릭하고 지도 이동
            const firstPlace = filteredData[0]; // 첫 번째 장소 가져오기
            const markerKey = `${firstPlace.lat},${firstPlace.lng}`; // 고유 키 생성
            const marker = markers.find(marker => {
                const markerLatLng = marker.getPosition();
                return (
                    Math.abs(markerLatLng.getLat() - firstPlace.lat) < 0.00001 && // 위도 비교
                    Math.abs(markerLatLng.getLng() - firstPlace.lng) < 0.00001    // 경도 비교
                );
                });

            if (marker) {
                kakao.maps.event.trigger(marker, "click"); // 첫 번째 마커의 클릭 이벤트 트리거
                map.setCenter(marker.getPosition()); // 첫 번째 마커 위치로 지도 중심 이동
            } else {
                console.error("First place's marker not found:", firstPlace);
            }

            // `fetchPlaces`와 같은 구조를 유지하기 위해
            // 필터링된 데이터를 저장하거나 콜백으로 사용하도록 수정 가능
            window.filteredPlaces = filteredData; // 전역 변수로 저장 (선택적)
        }
    }
    // searchPlaces를 전역으로 노출
    window.searchPlaces = searchPlaces;
});


