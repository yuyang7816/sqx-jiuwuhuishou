<template>
	<view style="padding-top: 200rpx;">
		<up-empty :mode="image" :text="contont">
		</up-empty>
	</view>
</template>

<script setup>
	defineProps({
		//内容
		contont: {
			type: String,
			default: '暂无数据'
		},
		//图片
		image: {
			type: String,
			default: 'list'
		}
	})
</script>

<style lang="scss">

</style>