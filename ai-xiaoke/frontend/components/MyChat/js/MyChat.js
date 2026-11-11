// document.addEventListener('DOMContentLoaded', function() );

function createFishDecorations() {
    return;
    const container = document.querySelector('.mychat_container');
    const fishCount = 5;
    
    for (let i = 0; i < fishCount; i++) {
        const fish = document.createElement('div');
        fish.className = 'fish-decoration';
        fish.style.left = `${Math.random() * 100}%`;
        fish.style.top = `${Math.random() * 100}%`;
        fish.style.animationDelay = `${Math.random() * 15}s`;
        fish.style.opacity = `${0.3 + Math.random() * 0.7}`;
        fish.style.width = `${20 + Math.random() * 40}px`;
        fish.style.height = `${10 + Math.random() * 20}px`;
        container.appendChild(fish);
    }
}
typeof window !== 'undefined' && (window.createFishDecorations = createFishDecorations);

function createRipple() {
    const characterContainer = document.querySelector('.character-container');
    if (!characterContainer) return;
    const ripple = document.createElement('div');
    ripple.className = 'ripple-effect';
    ripple.style.left = `${50 + Math.random() * 20 - 10}%`;
    ripple.style.top = `${50 + Math.random() * 20 - 10}%`;
    characterContainer.appendChild(ripple);
    
    setTimeout(() => {
        if (ripple && ripple.parentNode) ripple.remove();
    }, 6000);
}
typeof window !== 'undefined' && (window.createRipple = createRipple);

// function sendMessage() {
//     const textarea = document.querySelector('textarea');
//     const message = textarea.value.trim();
    
//     if (message) {
//         const chatContent = document.querySelector('.chat-content');
        
//         // 添加用户消息
//         const userBubble = document.createElement('div');
//         userBubble.className = 'message-bubble user';
//         userBubble.innerHTML = `
//             <div class="message-content">
//                 <p>${message}</p>
//             </div>
//         `;
//         chatContent.appendChild(userBubble);
        
//         // 清空输入框
//         textarea.value = '';
        
//         // 滚动到底部
//         chatContent.scrollTop = chatContent.scrollHeight;
        
//         // 模拟回复
//         setTimeout(() => {
//             const replyBubble = document.createElement('div');
//             replyBubble.className = 'message-bubble system';
//             replyBubble.innerHTML = `
//                 <div class="message-content">
//                     <p>已收到您的咨询，正在分析中...</p>
//                 </div>
//             `;
//             chatContent.appendChild(replyBubble);
//             chatContent.scrollTop = chatContent.scrollHeight;
//         }, 500);
//     }
// }
// typeof window !== 'undefined' && (window.sendMessage = sendMessage);

function showWaterQualityAnalysis() {
    const chatContent = document.querySelector('.chat-content');
    const analysisBubble = document.createElement('div');
    analysisBubble.className = 'message-bubble system';
    analysisBubble.innerHTML = `
        <div class="message-content">
            <p>水质分析结果：</p>
            <div class="data-grid">
                <div class="data-item">
                    <div class="data-label">溶解氧</div>
                    <div class="data-value">5.2 mg/L</div>
                    <div class="data-status good"></div>
                </div>
                <div class="data-item">
                    <div class="data-label">氨氮</div>
                    <div class="data-value">0.15 mg/L</div>
                    <div class="data-status good"></div>
                </div>
                <div class="data-item">
                    <div class="data-label">pH值</div>
                    <div class="data-value">7.6</div>
                    <div class="data-status good"></div>
                </div>
                <div class="data-item">
                    <div class="data-label">温度</div>
                    <div class="data-value">24.5°C</div>
                    <div class="data-status normal"></div>
                </div>
            </div>
            <p>建议：当前水质良好，继续保持监测频率。</p>
        </div>
    `;
    chatContent.appendChild(analysisBubble);
    chatContent.scrollTop = chatContent.scrollHeight;
}
typeof window !== 'undefined' && (window.showWaterQualityAnalysis = showWaterQualityAnalysis);

function showFarmingAdvice() {
    const chatContent = document.querySelector('.chat-content');
    const adviceBubble = document.createElement('div');
    adviceBubble.className = 'message-bubble system';
    adviceBubble.innerHTML = `
        <div class="message-content">
            <p>养殖优化建议：</p>
            <ul>
                <li>适当调整投喂量，避免过度投喂</li>
                <li>保持增氧设备正常运行</li>
                <li>定期检查鱼类健康状况</li>
                <li>根据季节变化调整养殖密度</li>
            </ul>
        </div>
    `;
    chatContent.appendChild(adviceBubble);
    chatContent.scrollTop = chatContent.scrollHeight;
}
typeof window !== 'undefined' && (window.showFarmingAdvice = showFarmingAdvice);

function showEquipmentManagement() {
    const chatContent = document.querySelector('.chat-content');
    const equipmentBubble = document.createElement('div');
    equipmentBubble.className = 'message-bubble system';
    equipmentBubble.innerHTML = `
        <div class="message-content">
            <p>设备管理建议：</p>
            <ul>
                <li>增氧设备：运行正常，建议每日运行8小时</li>
                <li>投喂设备：需要校准，误差率约5%</li>
                <li>水质监测仪：传感器需要清洁</li>
                <li>过滤系统：运行效率良好</li>
            </ul>
        </div>
    `;
    chatContent.appendChild(equipmentBubble);
    chatContent.scrollTop = chatContent.scrollHeight;
}
typeof window !== 'undefined' && (window.showEquipmentManagement = showEquipmentManagement);
