/**@wangyan*/
package com.yimu.blurfloatback;

/*
 * 该类为姿态传感器的静态工具类，提供静态方法来计算
 */
public class RotateUtil
{
	//angle为弧度 gVector  为重力向量[x,y,z,1]
	//返回值为旋转后的向量
	public static double[] pitchRotate(double angle,double[] gVector)
	{
		double[][] matrix=//绕x轴旋转变换矩阵
		{
		   {1,0,0,0},
		   {0,Math.cos(angle),Math.sin(angle),0},		   
		   {0,-Math.sin(angle),Math.cos(angle),0},		   //原来为：{0,-Math.sin(angle),Math.cos(angle),0},
		   {0,0,0,1}	
		};
		
		double[] tempDot={gVector[0],gVector[1],gVector[2],gVector[3]};
		for(int j=0;j<4;j++)
		{
			gVector[j]=(tempDot[0]*matrix[0][j]+tempDot[1]*matrix[1][j]+
			             tempDot[2]*matrix[2][j]+tempDot[3]*matrix[3][j]);    
		}
		return gVector;
	}
	//angle为弧度 gVector  为重力向量[x,y,z,1]
	//返回值为旋转后的向量
	public static double[] rollRotate(double angle,double[] gVector)
	{
		double[][] matrix=//绕y轴旋转变换矩阵
		{
		   {Math.cos(angle),0,-Math.sin(angle),0},
		   {0,1,0,0},
		   {Math.sin(angle),0,Math.cos(angle),0},
		   {0,0,0,1}	
		};
		double[] tempDot={gVector[0],gVector[1],gVector[2],gVector[3]};
		for(int j=0;j<4;j++)
		{
			gVector[j]=(tempDot[0]*matrix[0][j]+tempDot[1]*matrix[1][j]+
			             tempDot[2]*matrix[2][j]+tempDot[3]*matrix[3][j]);    
		}
		return gVector;
	}		
	//angle为弧度 gVector  为重力向量[x,y,z,1]
	//返回值为旋转后的向量
	public static double[] yawRotate(double angle,double[] gVector)
	{
		double[][] matrix=//绕z轴旋转变换矩阵
		{
		   {Math.cos(angle),Math.sin(angle),0,0},		   
		   {-Math.sin(angle),Math.cos(angle),0,0},
		   {0,0,1,0},
		   {0,0,0,1}	
		};
		double[] tempDot={gVector[0],gVector[1],gVector[2],gVector[3]};
		for(int j=0;j<4;j++)
		{
			gVector[j]=(tempDot[0]*matrix[0][j]+tempDot[1]*matrix[1][j]+
			             tempDot[2]*matrix[2][j]+tempDot[3]*matrix[3][j]);    
		}
		return gVector;
	}
	
	//物体坐标系->惯性坐标系
	//惯性——物体矩阵中，使用负的旋转角；物体——惯性矩阵中，使用正的旋转角
	//由于传来的是左手坐标系下的旋转角度，因此角度变负是为了左手到右手。
	public static float[] getDirectionDotLeftHand_ObjectsToInertia(double[] values,double[] vector)
	{
		double yawAngle=Math.toRadians(values[0]);//获取Yaw轴旋转角度弧度
		double pitchAngle=Math.toRadians(values[1]);//获取Pitch轴旋转角度弧度
		double rollAngle=Math.toRadians(values[2]);//获取Roll轴旋转角度弧度
		
		
		/*
		 * 两种复合方式是相等的，
		 * 记：
		 * 绕坐标系E下的x轴旋转α的旋转矩阵为Rx,
		 * 绕坐标系E下的y轴旋转β的旋转矩阵为Ry,
		 * 绕坐标系E下的z轴旋转r的旋转矩阵为Rz,
		 * 
		 * 绕坐标系E下的z轴旋转r的旋转矩阵为Rr（Rr=Rz），
		 * 绕 坐标系E在绕z轴旋转r后的新系E'下的y轴旋转β的旋转矩阵为Rb，
		 * 绕 坐标系E'在绕y轴旋转β后的新系E''下的x轴旋转α的旋转矩阵为Ra，
		 * 
		 * 
		 */
		
		//roll轴恢复
		vector=RotateUtil.rollRotate(rollAngle,vector);
		//pitch轴恢复
		vector=RotateUtil.pitchRotate(pitchAngle,vector);	
		//yaw轴恢复
		vector=RotateUtil.yawRotate(yawAngle,vector);
		
		//Log.v("gVector", "x:"+(int)gVector[0]+" y:"+(int)gVector[1]+" z:"+(int)gVector[2]);
		double mapX=vector[0];
		double mapY=vector[1];	
		double mapZ=vector[2];
		float[] result={(float)mapX,(float)mapY,(float)mapZ};
		return result;
	}	
	
	public static float getHeadBeta(float[] values){
		float camerBeta=0;
		float point[]={0,0,0};
		double[] lookvector={0,0,-100,1};//手机初始朝向
		double angle[]={0,0,0};//float 转double
		angle[0]=values[0];
		angle[1]=values[1];
		angle[2]=values[2];
		//传的是左手坐标系的角
		//物体坐标系->惯性坐标系
		point=RotateUtil.getDirectionDotLeftHand_ObjectsToInertia(angle,lookvector);
		camerBeta=(float) Math.toDegrees( Math.asin(point[2]/100f));
		
		
		return camerBeta;
	}
	
	
}