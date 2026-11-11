
function listIsEmpty(arr) {
	return !(arr && arr.length > 0);
}


// 避免空串
function timeKeepM(val) {
	try {
		if (!val)
			return "";
		let arr = val.split(" ")[1].split(":")
		arr = [arr[0], arr[1]];
		return arr.join(":");
	} catch(error) {
	    console.log(`groupHandle==========>`, error);
	}
	return "";
}

/**
 * 判断文件类型 0:文本;1:图片;2:视频;3:文档;4:pdf;99:其他;
 * @param {Object} fileUrl 文件地址
 */
const imgArr = [
  "bmp",
  "jpg",
  "jpeg",
  "png",
  "tif",
  "gif",
  "pcx",
  "tga",
  "exif",
  "fpx",
  "svg",
  "psd",
  "cdr",
  "pcd",
  "dxf",
  "ufo",
  "eps",
  "ai",
  "raw",
  "WMF",
  "webp",
  "avif",
  "apng",
];
const pdfArr = ["pdf"];
const videoArr = [
  "mp4",
  "rmvb",
  "vob",
  "avi",
  "wmv",
  "flv",
  "mkv",
  "webm",
  "mov",
];
const docArr = ["doc", "docx", "xls", "xlsx", "ppt", "pptx"];
function judgeFileType(fileUrl) {
  if (!fileUrl) return 0;
  fileUrl = fileUrl.toLowerCase();
  let flag = false;
  // 1. 图片判断
  imgArr.some((i) => (flag = fileUrl.endsWith(i)));
  if (flag) return 1;
  // 2. pdf判断
  pdfArr.some((i) => (flag = fileUrl.endsWith(i)));
  if (flag) return 4;
  // 3. 视频 判断
  videoArr.some((i) => (flag = fileUrl.endsWith(i)));
  if (flag) return 2;
  // 4. 文档 判断
  docArr.some((i) => (flag = fileUrl.endsWith(i)));
  if (flag) return 3;

  return 99;
}

// 对象是否包含key
function objHasKey(obj, key) {
  if (!obj || !key) return false;
  let flag = false;
  Object.keys(obj).forEach((ok, oi) => {
    if (ok == key) {
      flag = true;
      return;
    }
  });
  return flag;
}

// list 中查找一个值
function listFindOne(oList, oKey, oVal, func) {
  if (listIsEmpty(oList)) return null;
  let temp = null;
  oList.forEach((o, oi) => {
    if (oVal == o[oKey]) {
      temp = o;
      return temp;
    }
  });
  if(func) func(temp);
  return temp;
}

// list2list进行处理
function list2listHandle(oList, oKey, tList, tKey, fun) {
  if (listIsEmpty(oList) || listIsEmpty(tList)) return;
  tList.forEach((t, ti) => {
    let flag = false;
    oList.forEach((o, oi) => {
      if (t[tKey] == o[oKey]) {
        // console.log(`o:${JSON.stringify(o)}========>t:${JSON.stringify(t)}`);
        fun(o, t);
        flag = true;
        return;
      }
    });
    if (!flag) fun({}, t);
  });
}

// list 转换成 set
function listToSet(oList, key) {
  if (listIsEmpty(oList)) return oList;
  let n_oList = [];
  let o1 = JSON.parse(JSON.stringify(oList));
  let t1 = JSON.parse(JSON.stringify(oList));
  list2listHandle(o1, key, t1, key, (o, t) => {
    n_oList.push({ ...o, ...t });
  });
  let tList = [];
  let temp = [];
  n_oList.forEach((t, ti) => {
    if (temp.includes(key ? t[key] : t)) return;
    temp.push(key ? t[key] : t);
    tList.push(t);
  });
  return tList;
}

// 提示音
function playNotice() {
  var audio = new Audio("../../assets/media/notice.mp3");
  audio.play();
}

// 性能优化版本：使用缓存和预编译比较函数
const smartSortOptimized = (function() {
    // 缓存比较函数以提高性能
    const comparatorCache = new Map();
    
    /**
     * 获取或创建比较函数
     */
    function getComparator(property, isAscending) {
        const cacheKey = `${property || 'no-prop'}_${isAscending ? 'asc' : 'desc'}`;
        
        if (comparatorCache.has(cacheKey)) {
            return comparatorCache.get(cacheKey);
        }
        
        // 创建新的比较函数
        const comparator = property 
            ? createObjectComparator(property, isAscending)
            : createPrimitiveComparator(isAscending);
        
        comparatorCache.set(cacheKey, comparator);
        return comparator;
    }
    
    /**
     * 创建对象比较函数
     */
    function createObjectComparator(property, isAscending) {
        return function(a, b) {
            const valueA = a[property];
            const valueB = b[property];
            
            // 处理undefined/null值
            if (valueA == null && valueB == null) return 0;
            if (valueA == null) return 1;
            if (valueB == null) return -1;
            
            // 数字比较
            if (typeof valueA === 'number' && typeof valueB === 'number') {
                return isAscending ? valueA - valueB : valueB - valueA;
            }
            
            // 字符串比较
            const strA = String(valueA);
            const strB = String(valueB);
            return isAscending 
                ? strA.localeCompare(strB)
                : strB.localeCompare(strA);
        };
    }
    
    /**
     * 创建基本类型比较函数
     */
    function createPrimitiveComparator(isAscending) {
        return function(a, b) {
            if (a == null && b == null) return 0;
            if (a == null) return 1;
            if (b == null) return -1;
            
            if (typeof a === 'number' && typeof b === 'number') {
                return isAscending ? a - b : b - a;
            }
            
            const strA = String(a);
            const strB = String(b);
            return isAscending 
                ? strA.localeCompare(strB)
                : strB.localeCompare(strA);
        };
    }
    
    return function(array, property, order = 'asc') {
        // 参数验证
        if (!Array.isArray(array)) {
            throw new TypeError('第一个参数必须是数组');
        }
        
        if (array.length <= 1) {
            return array.slice(); // 使用slice进行浅拷贝
        }
        
        const isAscending = order.toLowerCase() === 'asc';
        const comparator = getComparator(property, isAscending);
        
        // 使用slice创建新数组并排序
        return array.slice().sort(comparator);
    };
})();


module.exports = {
  judgeFileType,
  objHasKey,
  listFindOne,
  list2listHandle,
  listToSet,
  playNotice,
  timeKeepM,
  smartSortOptimized, // 排序 list,属性,asc/desc
};
