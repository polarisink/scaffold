
#pragma once
#include <stdint.h>

#ifndef PDWLIB_H
#define PDWLIB_H

#ifdef DATAPROC_EXPORTS
#define PROC_API __declspec(dllexport)
#else
#define PROC_API __declspec(dllimport)
#endif


// 常量定义
#define PDW_NUM_OF_PACK  8100
#define MAX_SIG_PACKS  16
//#define PDW_NUM_OF_SIGPACK 2100


#pragma pack(push,1) //1字节对齐
//原始PDW数据直接提取
typedef struct
{
	uint8_t  Mark;
	uint64_t Toa;
	uint32_t Amp;
	uint32_t Pw;
	uint32_t Fre;
	uint32_t Fmin;
	uint32_t Fmax;
	uint16_t Angle;
}PDW_data;

/*数据包中所有的脉冲字直接解包后存放的结构体*/
typedef struct
{
	uint32_t PdwNum;    //脉冲字个数
	PDW_data Pdwdata[PDW_NUM_OF_PACK];
}Pdw_data_All;

//经计算后的脉冲参数
typedef struct
{
	uint8_t  mark;
	uint8_t  CWFlag;
	uint64_t toa;
	uint32_t ampp;
	uint32_t pw;
	uint32_t fre;
	uint32_t fmin;
	uint32_t fmax;
	uint32_t fre_cent;
	uint32_t BW;
	uint16_t angle;
}PdwPack;

/*数据包中所有的脉冲字直接解包后存放的结构体*/
typedef struct
{
	uint32_t PdwNum;    //脉冲字个数
	PdwPack PdwParseUnit[PDW_NUM_OF_PACK];
}PdwPack_All;


/*信号分选识别后的结果（信号特征）存放的结构体*/
typedef struct
{
	uint8_t mark;   //上位机暂不显示
	uint8_t CWFlag; //信号 连续波 标志 0-非连续波，1-连续波
	uint8_t pri_num;//信号 识别到的脉间间隔数量
	uint32_t pri[5];//信号 脉间间隔  实际脉间间隔(us)=pri/1000
	uint8_t pw_num; //信号 识别到的脉宽数量
	uint32_t pw[5]; //信号 脉宽  实际脉宽(us)=pw/1000
	uint8_t fre_num;//信号 识别到的中频数量
	uint32_t fre[5];//信号 中频频率  实际频率(MHz)=fre/1000
	uint8_t BW_num; //信号 识别到的带宽数量
	uint32_t BW[3]; //信号 带宽  实际带宽(MHz)=BW/1000
	uint16_t angle; //信号 方位角 实际方位角(°)= angle * 360 / 8192
	uint32_t amp;   //信号 幅度值 实际功率(dB)= 10*log(amp) - 0dB输入时测得的10*log(amp)值

}SigPart;

/*信号分选识别后的结果（脉冲 PDW数据）存放的结构体*/
/*此结构体中的参数的数组中的位置，就是按照脉冲PDW到达时间的先后排序的，可以直接从每个数组的0位置开始取*/
typedef struct
{
	uint32_t Num;				//当前识别信号的脉冲个数
	uint8_t  mark[PDW_NUM_OF_PACK];//脉冲PDW 标志位     上位机不显示
	uint8_t  CWFlag[PDW_NUM_OF_PACK];//脉冲PDW 连续波标志  0-否，1-是  上位机不显示
	uint64_t toa[PDW_NUM_OF_PACK];//脉冲PDW 到达时间 每153.75个代表1us，此处上位机使用时可以不按实际值计算，直接用脉冲序号递增代表脉冲时间先后即可。
	uint32_t ampp[PDW_NUM_OF_PACK];//脉冲PDW 幅度值 实际功率(dB)= 10*log(ampp) - 0dB输入时测得的10*log(amp)值
	uint32_t pw[PDW_NUM_OF_PACK];//脉冲PDW 脉宽  实际脉宽(us)= pw/153.75
	uint32_t fre[PDW_NUM_OF_PACK];//脉冲PDW 中频  实际频率(MHz)= fre*153.75/16384      上位机不显示
	uint32_t fmin[PDW_NUM_OF_PACK];//脉冲PDW 最小频率  实际频率(MHz)= fre*153.75/16384
	uint32_t fmax[PDW_NUM_OF_PACK];//脉冲PDW 最大频率  实际频率(MHz)= fre*153.75/16384
	uint32_t fre_cent[PDW_NUM_OF_PACK];//脉冲PDW 中心频率  实际频率(MHz)= fre*153.75/16384
	uint32_t BW[PDW_NUM_OF_PACK];//脉冲PDW 带宽  实际带宽(MHz)= fre*153.75/16384
	uint32_t delt_TOA[PDW_NUM_OF_PACK];//脉冲PDW      上位机不显示
	uint32_t delt_TOA1[PDW_NUM_OF_PACK];//脉冲PDW 脉间间隔 实际脉间间隔(us)= delt_TOA1 / 153.75
	uint16_t angle[PDW_NUM_OF_PACK];//脉冲PDW 方位角 实际方位角(°)= angle * 360 / 8192
	uint16_t angle_max;//脉冲PDW      上位机不显示
	SigPart Sig_Part;

}SigPack;

/*信号分选识别后的结果存放的结构体，最多保存16个信号*/
typedef struct
{
	uint32_t SigNum;    //实际识别到的信号个数
	SigPack Sig_Pack[MAX_SIG_PACKS];//对应识别到的信号的空间
}SigPack_All;

#pragma pack(pop)

//核心处理函数：输入->缓存->输出


#ifdef __cplusplus
extern "C"
{
#endif

	PROC_API unsigned int PdwParse(Pdw_data_All* pdw_data, PdwPack_All* pdw_pack, SigPack_All* sig_pack,double min_pw, unsigned int  max_dtoa, unsigned int  min_dtoa);//
	//min_pw：  信号侦察识别最小脉宽 ----可通过界面控件设置
	//max_dtoa：信号侦察识别最大脉间间隔 ----可通过界面控件设置或者后台默认
	//min_dtoa：信号侦察识别最小脉间间隔 ----可通过界面控件设置或者后台默认

#ifdef __cplusplus
}
#endif


#endif