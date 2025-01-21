{   // 작성 버튼 처리
    const writeButton = document.querySelector(".write_Btn_item");
    if (writeButton) {
        writeButton.addEventListener("click", () => {
            window.location.href = "/zip/write";
        });
    }
}

{   // 정렬 버튼 처리
    const $radioContainer = document.querySelector('.community_radioContainer');

    $radioContainer.addEventListener("change", (e) => {
        if (e.target.classList.contains("radio_myRadio")) {
            const searchParams = new URLSearchParams(location.search);
            searchParams.set("order", e.target.value);

            location.search = searchParams.toString();
        }
    });
}


document.addEventListener("DOMContentLoaded", function () {
    const tabLinks = document.querySelectorAll(".SubView-tab-t1");

    // 현재 URL에서 category 값을 가져옴
    const currentCategory = new URL(window.location.href).searchParams.get("category");

    // 새로고침 후 URL의 category 값과 data-category 값을 비교하여 active 추가
    tabLinks.forEach((tab) => {
        if (tab.dataset.category === currentCategory) {
            tab.classList.add("active");
        }

        // 탭 클릭 시 active 클래스 업데이트
        tab.addEventListener("click", function () {
            tabLinks.forEach((link) => link.classList.remove("active"));
            this.classList.add("active");
        });
    });
});












/*document.addEventListener("DOMContentLoaded", () => {
    const $qaListDateTime = document.querySelectorAll(".qaList_communityDate");

    $qaListDateTime.forEach(element => {
        const zipUpdatedAt = element.getAttribute("data-updated-at");

        if (zipUpdatedAt) {
            const formattedTime = timeForToday(zipUpdatedAt);

            element.textContent = `수정일 : ${formattedTime}`;
        }
    });
});

function timeForToday(value) {
    const today = new Date();
    const timeValue = new Date(value);


    const betweenTime = Math.floor((today.getTime() - timeValue.getTime()) / 1000 / 60);

    if (betweenTime < 1) { return "방금 전"; }

    if (betweenTime < 60) {
        return `${betweenTime}분 전`;
    }

    const betweenTimeHour = Math.floor(betweenTime / 60);
    if (betweenTimeHour < 24) {
        return `${betweenTimeHour}시간 전`;
    }

    const betweenTimeDay = Math.floor(betweenTimeHour / 24);
    if (betweenTimeDay < 365) {
        return `${betweenTimeDay}일 전`;
    }

    return `${Math.floor(betweenTimeDay / 365)}년 전`;
}*/







