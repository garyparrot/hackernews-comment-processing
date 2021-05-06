let stompClient = null;

function setConnected(connected) {
    console.log("Current state: ", connected);
}

function connect() {
    let socket = new SockJS('/gs-guide-websocket');

    stompClient = Stomp.over(socket);
    stompClient.debug = null;
    stompClient.connect({}, function (frame) {
        setConnected(true);
        stompClient.subscribe('/stream/keyword-matches', function(content) {
            onReceiveKeywordMatchUpdate(JSON.parse(content.body));
        });
    });
}

function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    setConnected(false);
}

function onReceiveKeywordMatchUpdate(keywordMatch) {
    console.log(keywordMatch);
    addEntryToList(keywordMatch);
}

function linkToHackerNewsItem(id) {
    return "https://hacker-news.firebaseio.com/v0/item/"+id+".json?print=pretty";
}

function addEntryToList(keywordMatch) {
    let newItem = document.createElement("div");
    let newLink = document.createElement("a");
    newLink.innerText = keywordMatch.category;
    newLink.href = linkToHackerNewsItem(keywordMatch.hacker_news_item_id);
    newItem.classList.add('row')
    newItem.appendChild(newLink);

    let sectionDom = document.getElementById("section");
    sectionDom.insertBefore(newItem, sectionDom.firstElementChild);
    sectionDom.removeChild(sectionDom.lastElementChild);
}

$(function () {
    connect();
})

