const $postList = document.querySelectorAll('.post-card');

$postList.forEach(post => {
    post.addEventListener('click', function () {
       const helpId = this.dataset.helpId;

        location.href=`/help/detail?helpId=${helpId}`;
    });
})
function goToDetail(helpId) {
    if(helpId) {
        location.href = `/help/detail?helpId=${helpId}`;
    }
}
document.addEventListener('DOMContentLoaded', function() {
    // 게시글 카드 클릭 이벤트 처리
    const postCards = document.querySelectorAll('.post-card');
    postCards.forEach(card => {
        card.addEventListener('click', function() {
            const helpId = this.getAttribute('data-help-id');
            window.location.href = `/help/detail?helpId=${helpId}`;
        });
    });
});