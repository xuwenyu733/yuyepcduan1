<template>
  <!-- <iframe id='external_h5' src="/PMIS/rapid_dev/customForm/data_coming.html"
      style="width:100%;height:2500px;border: none;"
    ></iframe> -->
  <iframe
    id="external_h5"
    :src="src"
    style="width: 100%; height: 2500px; border: none"
  ></iframe>
  <!-- <div class="iframe-container">
    <p>abc</p>
  </div> -->
</template>

<script>
import Cookies from "js-cookie";
export default {
  name: "iframe",
  data() {
    return {
      src: "/zx/customForm/formRecords1.html?a=1&tblname=learn_notifications&R=9",
      // 版本号
      version: "3.6.4",
    };
  },
  created() {
    console.log("===888888888===========");
    console.log(this.$route.meta.iframe_prop);
    if (this.$route.meta.iframe_prop == "item1") {
      this.src =
        "/zx/customForm/formRecords1.html?a=1&tblname=learn_notifications&R=9";
    } else if (this.$route.meta.iframe_prop == "item2") {
      this.src =
        "/zx/customForm/formRecords1.html?a=1&tblname=fish_pool_stock&R=88701.5483049025";
    } else if (this.$route.meta.iframe_prop == "item3") {
      this.src =
        "/zx/customForm/formRecords1.html?a=1&tblname=threshold_config&R=90226.38608668608";
    }
    // console.log("===999===========",Cookies.get("user_name"));
    //this.src='/index';
    //window.location="/wiidu/PMIS/indexdev.htm"
    //document.getElementById('external_h5').setAttribute('src','/wiidu/PMIS/indexdev.htm');
  },
  mounted() {
    // console.log("===999123666===========",Cookies.get("user_name"));
    //this.src="/PMIS/rapid_dev/customForm/datalist.html?a=1&tblname=construction_diary3&R=9";
    //console.log(this.src);
    //this.getId(options);
  },
  methods: {
    goTarget(href) {
      window.open(href, "_blank");
    },
  },
};
</script>

<style scoped lang="scss">
.home {
  blockquote {
    padding: 10px 20px;
    margin: 0 0 20px;
    font-size: 17.5px;
    border-left: 5px solid #eee;
  }
  hr {
    margin-top: 20px;
    margin-bottom: 20px;
    border: 0;
    border-top: 1px solid #eee;
  }
  .col-item {
    margin-bottom: 20px;
  }

  ul {
    padding: 0;
    margin: 0;
  }

  font-family: "open sans", "Helvetica Neue", Helvetica, Arial, sans-serif;
  font-size: 13px;
  color: #676a6c;
  overflow-x: hidden;

  ul {
    list-style-type: none;
  }

  h4 {
    margin-top: 0px;
  }

  h2 {
    margin-top: 10px;
    font-size: 26px;
    font-weight: 100;
  }

  p {
    margin-top: 10px;

    b {
      font-weight: 700;
    }
  }

  .update-log {
    ol {
      display: block;
      list-style-type: decimal;
      margin-block-start: 1em;
      margin-block-end: 1em;
      margin-inline-start: 0;
      margin-inline-end: 0;
      padding-inline-start: 40px;
    }
  }
}
</style>

