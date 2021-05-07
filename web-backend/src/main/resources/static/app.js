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
    return "https://hacker-news.firebaseio.com/v0/item/"+id+".json";
}

function linkToHackerNewsComment(id) {
    return "https://news.ycombinator.com/item?id=" + id;
}

const highlightStyle = "color: #F4F4F4; background-color: #333333"

function highlightKeyword (content, regex) {
    return String(content).replace(regex, function (match) {
        return "<span style='"+ highlightStyle +"'>" + match + "</span>";
    });
}

function addEntryToList(keywordMatch) {
    const template = document.getElementById("KeywordMatchCard");
    const newItem = template.content.firstElementChild.cloneNode(true);

    // TODO: fix the hack
    itemId = keywordMatch.hackerNewsItemId || keywordMatch.hacker_news_item_id;

    newItem.getElementsByClassName("card-title")[0]
        .innerHTML = keywordMatch.category;
    newItem.getElementsByClassName("card-subtitle")[0]
        .innerHTML = keywordMatch.keyword;
    newItem.getElementsByClassName("card-text")[0]
        .innerHTML = "...";
    newItem.getElementsByClassName("card-link")[0]
        .href = linkToHackerNewsComment(itemId);

    queryHackerNewsItem(itemId, (item) => {
        text = item.text;
        text = highlightKeyword(text, new RegExp("\\b"+keywordMatch.keyword+"\\b", 'i'), '#00FF00');
        newItem.getElementsByClassName("card-text")[0]
            .innerHTML = text;
    });

    let sectionDom = document.getElementById("section");
    sectionDom.insertBefore(newItem, sectionDom.firstElementChild);

    if (sectionDom.childElementCount > 20)
        sectionDom.removeChild(sectionDom.lastElementChild);
}

function queryHackerNewsItem(itemId, callback) {
    const url = linkToHackerNewsItem(itemId);
    axios.get(url).then((response) => callback(response.data))
}

$(function () {
    axios.get("/v0/keyword-matches/recent", { params: { page: 0 } })
        .then(function (response) {
            response.data.forEach((item) => addEntryToList(item))
        })
    connect();
})

