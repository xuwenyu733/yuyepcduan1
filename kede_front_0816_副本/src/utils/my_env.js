let PROCESS_ENV_OBJ = {isDev: false};
function setIsDev(obj) {
    PROCESS_ENV_OBJ.isDev = obj;
    return PROCESS_ENV_OBJ.isDev;
}
function isDev() {
    // return process.env.NODE_ENV == 'development';
    return PROCESS_ENV_OBJ.isDev;
}

// export default {
//     setIsDev,
//     isDev
// }

module.exports = {
    isDev,
    setIsDev
}