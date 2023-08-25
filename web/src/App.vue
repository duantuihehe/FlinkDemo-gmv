<template>  
  <div class="count-container">  
    <span>销售额:</span>
    <br>
    <span ref="countupRef"></span>  
    
  </div>  
</template>

<script setup lang="ts">
  import { CountUp } from 'countup.js'
  import type { CountUpOptions } from 'countup.js'
  import { onMounted, ref } from 'vue'
  import io from 'socket.io-client';

  let numAnim = ref(null) as any
  var startNum=0
  const countupRef = ref()
  const props = defineProps({
    end: {
      type: Number,
      default: 0
    },
    options: {
      type: Object,
      validator(option: Object) {
        let keys = ['startVal', 'decimalPlaces', 'duration', 'useGrouping', 'useEasing', 'smartEasingThreshold', 'smartEasingAmount', 'separator', 'decimal', 'prefix', 'suffix', 'numerals']
        for (const key in option) {
          if (!keys.includes(key)) {
            console.error(" CountUp 传入的 options 值不符合 CountUpOptions")
            return false
          }
        }
        return true
      },
      default() {
        let options: CountUpOptions = {
          startVal: 0, // 开始的数字  一般设置0开始
          decimalPlaces: 2, // number类型 小数位，整数自动添.00
          duration: 2, // number类型 动画延迟秒数，默认值是2
          useGrouping: true, // boolean类型  是否开启逗号，默认true(1,000)false(1000)
          useEasing: true,  // booleanl类型 动画缓动效果(ease),默认true
          smartEasingThreshold: 500, // numberl类型 大于这个数值的值开启平滑缓动
          smartEasingAmount: 300, // numberl类型
          separator: ',',// string 类型 分割用的符号
          decimal: '.', // string 类型  小数分割符合
          prefix: '', // sttring 类型  数字开头添加固定字符
          suffix: '', // sttring类型 数字末尾添加固定字符
          numerals: []  // Array类型 替换从0到9对应的字，也就是自定数字字符了,数组存储
        }
        return options
      }
    }
  })
  onMounted(() => {
    initCount()
    getData()
  })
  const initCount = () => {
    numAnim = new CountUp(countupRef.value, props.end, props.options)
    numAnim.start()
  }

  const getData= ()=>{
    const socket = io('http://localhost:9000'); // 将服务器地址更改为实际的Socket.io服务器地址
    console.info(socket)
    socket.on('message', (data) => {
      console.info(startNum)
      console.info(data)

      props.options.startVal=startNum
      startNum=data
      
      numAnim = new CountUp(countupRef.value, data, props.options)
    numAnim.start()
    });
  }
</script>

  
<style scoped>  
  .count-container {  
    width: 100%; /* 填充整个屏幕宽度 */  
    height: 100vh; /* 占据整个屏幕高度 */  
    display: flex;  
    align-items: center;  
    justify-content: center;  
    font-size: 36px; /* 你可以根据需要调整这个值 */  
  }  
  
 
  span{
    word-break:normal; 
    width:auto; 
    display:block; 
    white-space:pre-wrap;
    word-wrap : break-word ;
    overflow: hidden ;
}  
</style>