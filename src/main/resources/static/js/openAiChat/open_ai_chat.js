// DOM 요소 가져오기
const chatContainer = document.getElementById('openaiChatContainer');
const messageInput = document.getElementById('openaiMessageInput');
const chatMessages = document.getElementById('openaiChatMessages');

// 메시지 추가하는 함수
function addMessage(message, type) {
    const messageDiv = document.createElement('div');
    messageDiv.classList.add('openai-message');
    messageDiv.classList.add(type === 'user' ? 'openai-user-message' : 'openai-bot-message');


    let formattedMessage = message
        .replace(/\n/g, '<br>')
        .replace(/([.!?]) /g, '$1<br><br>');

    // 정규표현식
    const urlRegex = /http:\/\/localhost:10000\/[a-z]+\/[a-z]+(?=[^a-z]|$)/g;
    const messageWithLinks = formattedMessage.replace(urlRegex, function(url) {
        return `<a href="${url}" target="_blank" class="openai-chat-link">${url}</a>`;
    });

    messageDiv.innerHTML = messageWithLinks;
    chatMessages.appendChild(messageDiv);
    chatMessages.scrollTop = chatMessages.scrollHeight;
}

// 채팅창 열고 닫기
function toggleChat() {
    chatContainer.classList.toggle('active');
    if (chatContainer.classList.contains('active')) {
        messageInput.focus();
    }
}

// 메시지 전송하기
async function sendMessage() {
    const message = messageInput.value.trim();
    if (!message) return;

    // 사용자 메시지 추가
    addMessage(message, 'user');
    messageInput.value = '';

    try {
        const response = await fetch('/chat2', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                message: message
            })
        });

        if (!response.ok) {
            throw new Error('서버 응답 오류');
        }

        const botResponse = await response.text();
        const contentMatch = botResponse.match(/content=([^)]+)\)\]$/);
        const content = contentMatch ? contentMatch[1] : botResponse;
        addMessage(botResponse, 'bot');

    } catch (error) {
        console.error('Error:', error);
        addMessage('죄송합니다. 오류가 발생했습니다. 다시 시도해 주세요.', 'bot');
    }
}

// 엔터 키로 메시지 전송
messageInput.addEventListener('keypress', function(e) {
    if (e.key === 'Enter') {
        sendMessage();
    }
});


window.OpenAIChat = {
    toggleChat: toggleChat,
    sendMessage: sendMessage
};