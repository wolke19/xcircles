const fileSelector = document.getElementById('file-selector');
fileSelector.addEventListener('change', (event) => {
    const fileList = event.target.files;
    for (let file of fileList) {
        console.log(file)
        readText(file)
    }
});

function readText(file) {
    const reader = new FileReader();
    reader.addEventListener('load', (event) => {
        res = event.target.result + '';
        let igc_txt = atob(res.split(',')[1]);
        let lr = countCircles(igc_txt)

        alert('Rechtskreise: ' + lr.right + ' Linkskreise: ' + lr.left);
    });
    reader.readAsDataURL(file);
}

function countCircles(igc_txt) {
    koordArray = []

    for (let line of igc_txt.split('\n')) {
        if (line[0] == 'B') {
            koordArray.push(line)
        }
    }

    let xyPosArray = new Array(koordArray.length).fill().map(() => new Array(2));

    for (let i = 0; i < xyPosArray.length - 1; i++) {

        let oldLine = koordArray[i];
        let line = koordArray[i + 1];

        xyPosArray[i][0] = parseInt(line.substring(7, 14)) - parseInt(oldLine.substring(7, 14));
        xyPosArray[i][1] = parseInt(line.substring(15, 23)) - parseInt(oldLine.substring(15, 23));

        // Drehrichtung unter Beachtung der N/S E/W Koordinatenangaben berichtigen
        let northSouth = line.charAt(14);
        let westEast = line.charAt(23);
        if (northSouth === 'S') {
            xyPosArray[i][0] = -xyPosArray[i][0];
        }
        if (westEast === 'W') {
            xyPosArray[i][1] = -xyPosArray[i][1];
        }
    }

    let interval = calcInterval(koordArray)
    let turnDuration = 38
    console.log("Interval: " + interval + "turnDuration(Parameter): " + turnDuration)

    return calcLeftRightTurns(xyPosArray, interval, turnDuration)
}

function calcInterval(koordArray) {
    return parseInt(koordArray[1].substring(5, 7) - koordArray[0].substring(5, 7))
}

function calcLeftRightTurns(v, interval, turnDuration) {
    let leftRightTurns = {
        left: 0,
        right: 0
    }
    let timeCounter = 0;
    let angleCounter = 0;

    for (let i = 1; i < v.length; i++) {

        angleCounter += changeOfDirInDeg(v[i - 1][0], v[i - 1][1], v[i][0], v[i][1]);
        timeCounter++;

        if (timeCounter > (turnDuration / interval) && angleCounter < 360 && angleCounter > -360) {
            angleCounter = 0;
            timeCounter = 0;
        }
        else if (angleCounter >= 360) {
            leftRightTurns.left++;
            angleCounter -= 360;
            timeCounter = 0;
        }
        else if (angleCounter <= -360) {
            leftRightTurns.right++;
            angleCounter += 360;
            timeCounter = 0;
        }
    }
    return leftRightTurns;
}

function radians_to_degrees(radians) {
    return radians * (180 / Math.PI);
}

function changeOfDirInDeg(n1, e1, n2, e2) {

    let kreuzprodukt = n1 * e2 - e1 * n2;

    if (kreuzprodukt == 0) {
        return 0;
    }

    let skalarprodukt = n1 * n2 + e1 * e2;
    let betragV1 = Math.sqrt(n1 * n1 + e1 * e1);
    let betragV2 = Math.sqrt(n2 * n2 + e2 * e2);

    let richtunsAenderung = radians_to_degrees(Math.acos((skalarprodukt / (betragV1 * betragV2))));

    if (kreuzprodukt < 0) {
        richtunsAenderung *= -1;
    }

    return richtunsAenderung;
}
