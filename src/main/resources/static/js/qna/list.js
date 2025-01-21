{   // 권한 없음 오류 처리
    const search = location.search;
    const searchParams = new URLSearchParams(search);

    const error = searchParams.get("error");

    if (error && error === 'unauthorized') {
        alert("내 글만 확인할 수 있습니다!");
    }
}



















