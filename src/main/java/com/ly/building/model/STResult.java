package com.ly.building.model;

public class STResult {


    /**
     * user_ID : 0
     * slice_ID : 1
     * mode : hybrid mode
     * meanV : 25.6538
     * meanA : -0.0146
     * meanTA : 0.1442
     * meanSinuosity : 1000.0001
     * var_V : 48.5231
     * var_A : 19.3887
     * var_TA : 3298.6454
     * var_Sinuosity : 0.0
     * mode_V : 63.7807
     * mode_A : -2.6945
     * mode_TA : -1.8265
     * mode_Sinuosity : 1000.0
     * Min1_V : 0.0161
     * Min2_V : 0.0457
     * Min3_V : 0.1308
     * Max1_V : 698.6809
     * Max2_V : 63.7807
     * Max3_V : 56.0264
     * Min1_A : -17.7505
     * Min2_A : -15.028
     * Min3_A : -12.698
     * Max1_A : 57.0723
     * Max2_A : 2.5419
     * Max3_A : 2.0454
     * Min1_TA : -318.9452
     * Min2_TA : -312.0643
     * Min3_TA : -310.6617
     * Max1_TA : 204.8153
     * Max2_TA : 184.9109
     * Max3_TA : 181.2246
     * Min1_Sinuosity : 999.9936
     * Min2_Sinuosity : 999.9973
     * Min3_Sinuosity : 999.9976
     * Max1_Sinuosity : 1000.0115
     * Max2_Sinuosity : 1000.0049
     * Max3_Sinuosity : 1000.0042
     * ValueRange_V : 698.6648
     * ValueRange_A : 74.8228
     * ValueRange_TA : 523.7605
     * ValueRange_Sinuosity : 0.0179
     * lowQua_V : 18.7926
     * upQua_V : 31.4997
     * RangeQua_V : 12.7071
     * lowQua_A : -0.0951
     * upQua_A : 0.0459
     * RangeQua_A : 0.141
     * lowQua_TA : -13.0535
     * upQua_TA : 12.6319
     * RangeQua_TA : 25.6854
     * lowQua_Sinuosity : 1000.0
     * upQua_Sinuosity : 1000.0
     * RangeQua_Sinuosity : 0.0
     * Skew_V : 12.9723
     * Skew_A : 9.9974
     * Skew_TA : -1.5493
     * Skew_Sinuosity : 3.9005
     * Kurt_V : 180.2918
     * Kurt_A : 139.7791
     * Kurt_TA : 14.5456
     * Kurt_Sinuosity : 44.596
     * CV_V : 0.5287
     * CV_A : -8.0E-4
     * CV_TA : 0.0
     * CV_Sinuosity : 6.885045884097E8
     * AutoCC_V : 0.0848
     * AutoCC_A : -0.1661
     * AutoCC_TA : -0.3254
     * AutoCC_Sinuosity : -0.0686
     * HCR : 0.1304
     * SR : 1.0
     * VCR : 0.0
     * length : 207
     */

    private String user_ID;//用户id
    private String slice_ID;//轨迹编号
    private String mode;//出行方式
    private double meanV;//平均速度
    private double meanA;//平均加速度
    private double meanTA;//平均转角
    private double meanSinuosity;//平均弯度
    private double var_V;//速度的标准差
    private double var_A;//加速度的标准差
    private double var_TA;//转角的标准差
    private double var_Sinuosity;//弯度的标准差
    private double mode_V;//
    private double mode_A;
    private double mode_TA;
    private double mode_Sinuosity;
    private double Min1_V;//最小速度
    private double Min2_V;
    private double Min3_V;
    private double Max1_V;//最大速度
    private double Max2_V;
    private double Max3_V;
    private double Min1_A;//最小加速度
    private double Min2_A;
    private double Min3_A;
    private double Max1_A;//最大加速度
    private double Max2_A;
    private double Max3_A;
    private double Min1_TA;//最小转角
    private double Min2_TA;
    private double Min3_TA;
    private double Max1_TA;//最大转角
    private double Max2_TA;
    private double Max3_TA;
    private double Min1_Sinuosity;//最小弯度
    private double Min2_Sinuosity;
    private double Min3_Sinuosity;
    private double Max1_Sinuosity;//最大弯度
    private double Max2_Sinuosity;
    private double Max3_Sinuosity;
    private double ValueRange_V;
    private double ValueRange_A;
    private double ValueRange_TA;
    private double ValueRange_Sinuosity;
    private double lowQua_V;
    private double upQua_V;
    private double RangeQua_V;
    private double lowQua_A;
    private double upQua_A;
    private double RangeQua_A;
    private double lowQua_TA;
    private double upQua_TA;
    private double RangeQua_TA;
    private double lowQua_Sinuosity;
    private double upQua_Sinuosity;
    private double RangeQua_Sinuosity;
    private double Skew_V;//速度的偏度
    private double Skew_A;//加速度的偏度
    private double Skew_TA;//转角的偏度
    private double Skew_Sinuosity;//弯度的偏度
    private double Kurt_V;//速度的峰度
    private double Kurt_A;//加速度的峰度
    private double Kurt_TA;//转角的峰度
    private double Kurt_Sinuosity;//弯度的峰度
    private double CV_V;
    private double CV_A;
    private double CV_TA;
    private double CV_Sinuosity;
    private double AutoCC_V;
    private double AutoCC_A;
    private double AutoCC_TA;
    private double AutoCC_Sinuosity;
    private double HCR;//方向变化率
    private double SR;//停止率
    private double VCR;//速度变化率
    private int length;//轨迹长度

    private double stHCR;//方向变化率
    private double stSR;//停止率
    private double stVCR;//速度变化率
    private int stLength;//轨迹长度

    public double getStHCR() {
        return stHCR;
    }

    public void setStHCR(double stHCR) {
        this.stHCR = stHCR;
    }

    public double getStSR() {
        return stSR;
    }

    public void setStSR(double stSR) {
        this.stSR = stSR;
    }

    public double getStVCR() {
        return stVCR;
    }

    public void setStVCR(double stVCR) {
        this.stVCR = stVCR;
    }

    public int getStLength() {
        return stLength;
    }

    public void setStLength(int stLength) {
        this.stLength = stLength;
    }

    public String getUser_ID() {
        return user_ID;
    }

    public void setUser_ID(String user_ID) {
        this.user_ID = user_ID;
    }

    public String getSlice_ID() {
        return slice_ID;
    }

    public void setSlice_ID(String slice_ID) {
        this.slice_ID = slice_ID;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public double getMeanV() {
        return meanV;
    }

    public void setMeanV(double meanV) {
        this.meanV = meanV;
    }

    public double getMeanA() {
        return meanA;
    }

    public void setMeanA(double meanA) {
        this.meanA = meanA;
    }

    public double getMeanTA() {
        return meanTA;
    }

    public void setMeanTA(double meanTA) {
        this.meanTA = meanTA;
    }

    public double getMeanSinuosity() {
        return meanSinuosity;
    }

    public void setMeanSinuosity(double meanSinuosity) {
        this.meanSinuosity = meanSinuosity;
    }

    public double getVar_V() {
        return var_V;
    }

    public void setVar_V(double var_V) {
        this.var_V = var_V;
    }

    public double getVar_A() {
        return var_A;
    }

    public void setVar_A(double var_A) {
        this.var_A = var_A;
    }

    public double getVar_TA() {
        return var_TA;
    }

    public void setVar_TA(double var_TA) {
        this.var_TA = var_TA;
    }

    public double getVar_Sinuosity() {
        return var_Sinuosity;
    }

    public void setVar_Sinuosity(double var_Sinuosity) {
        this.var_Sinuosity = var_Sinuosity;
    }

    public double getMode_V() {
        return mode_V;
    }

    public void setMode_V(double mode_V) {
        this.mode_V = mode_V;
    }

    public double getMode_A() {
        return mode_A;
    }

    public void setMode_A(double mode_A) {
        this.mode_A = mode_A;
    }

    public double getMode_TA() {
        return mode_TA;
    }

    public void setMode_TA(double mode_TA) {
        this.mode_TA = mode_TA;
    }

    public double getMode_Sinuosity() {
        return mode_Sinuosity;
    }

    public void setMode_Sinuosity(double mode_Sinuosity) {
        this.mode_Sinuosity = mode_Sinuosity;
    }

    public double getMin1_V() {
        return Min1_V;
    }

    public void setMin1_V(double Min1_V) {
        this.Min1_V = Min1_V;
    }

    public double getMin2_V() {
        return Min2_V;
    }

    public void setMin2_V(double Min2_V) {
        this.Min2_V = Min2_V;
    }

    public double getMin3_V() {
        return Min3_V;
    }

    public void setMin3_V(double Min3_V) {
        this.Min3_V = Min3_V;
    }

    public double getMax1_V() {
        return Max1_V;
    }

    public void setMax1_V(double Max1_V) {
        this.Max1_V = Max1_V;
    }

    public double getMax2_V() {
        return Max2_V;
    }

    public void setMax2_V(double Max2_V) {
        this.Max2_V = Max2_V;
    }

    public double getMax3_V() {
        return Max3_V;
    }

    public void setMax3_V(double Max3_V) {
        this.Max3_V = Max3_V;
    }

    public double getMin1_A() {
        return Min1_A;
    }

    public void setMin1_A(double Min1_A) {
        this.Min1_A = Min1_A;
    }

    public double getMin2_A() {
        return Min2_A;
    }

    public void setMin2_A(double Min2_A) {
        this.Min2_A = Min2_A;
    }

    public double getMin3_A() {
        return Min3_A;
    }

    public void setMin3_A(double Min3_A) {
        this.Min3_A = Min3_A;
    }

    public double getMax1_A() {
        return Max1_A;
    }

    public void setMax1_A(double Max1_A) {
        this.Max1_A = Max1_A;
    }

    public double getMax2_A() {
        return Max2_A;
    }

    public void setMax2_A(double Max2_A) {
        this.Max2_A = Max2_A;
    }

    public double getMax3_A() {
        return Max3_A;
    }

    public void setMax3_A(double Max3_A) {
        this.Max3_A = Max3_A;
    }

    public double getMin1_TA() {
        return Min1_TA;
    }

    public void setMin1_TA(double Min1_TA) {
        this.Min1_TA = Min1_TA;
    }

    public double getMin2_TA() {
        return Min2_TA;
    }

    public void setMin2_TA(double Min2_TA) {
        this.Min2_TA = Min2_TA;
    }

    public double getMin3_TA() {
        return Min3_TA;
    }

    public void setMin3_TA(double Min3_TA) {
        this.Min3_TA = Min3_TA;
    }

    public double getMax1_TA() {
        return Max1_TA;
    }

    public void setMax1_TA(double Max1_TA) {
        this.Max1_TA = Max1_TA;
    }

    public double getMax2_TA() {
        return Max2_TA;
    }

    public void setMax2_TA(double Max2_TA) {
        this.Max2_TA = Max2_TA;
    }

    public double getMax3_TA() {
        return Max3_TA;
    }

    public void setMax3_TA(double Max3_TA) {
        this.Max3_TA = Max3_TA;
    }

    public double getMin1_Sinuosity() {
        return Min1_Sinuosity;
    }

    public void setMin1_Sinuosity(double Min1_Sinuosity) {
        this.Min1_Sinuosity = Min1_Sinuosity;
    }

    public double getMin2_Sinuosity() {
        return Min2_Sinuosity;
    }

    public void setMin2_Sinuosity(double Min2_Sinuosity) {
        this.Min2_Sinuosity = Min2_Sinuosity;
    }

    public double getMin3_Sinuosity() {
        return Min3_Sinuosity;
    }

    public void setMin3_Sinuosity(double Min3_Sinuosity) {
        this.Min3_Sinuosity = Min3_Sinuosity;
    }

    public double getMax1_Sinuosity() {
        return Max1_Sinuosity;
    }

    public void setMax1_Sinuosity(double Max1_Sinuosity) {
        this.Max1_Sinuosity = Max1_Sinuosity;
    }

    public double getMax2_Sinuosity() {
        return Max2_Sinuosity;
    }

    public void setMax2_Sinuosity(double Max2_Sinuosity) {
        this.Max2_Sinuosity = Max2_Sinuosity;
    }

    public double getMax3_Sinuosity() {
        return Max3_Sinuosity;
    }

    public void setMax3_Sinuosity(double Max3_Sinuosity) {
        this.Max3_Sinuosity = Max3_Sinuosity;
    }

    public double getValueRange_V() {
        return ValueRange_V;
    }

    public void setValueRange_V(double ValueRange_V) {
        this.ValueRange_V = ValueRange_V;
    }

    public double getValueRange_A() {
        return ValueRange_A;
    }

    public void setValueRange_A(double ValueRange_A) {
        this.ValueRange_A = ValueRange_A;
    }

    public double getValueRange_TA() {
        return ValueRange_TA;
    }

    public void setValueRange_TA(double ValueRange_TA) {
        this.ValueRange_TA = ValueRange_TA;
    }

    public double getValueRange_Sinuosity() {
        return ValueRange_Sinuosity;
    }

    public void setValueRange_Sinuosity(double ValueRange_Sinuosity) {
        this.ValueRange_Sinuosity = ValueRange_Sinuosity;
    }

    public double getLowQua_V() {
        return lowQua_V;
    }

    public void setLowQua_V(double lowQua_V) {
        this.lowQua_V = lowQua_V;
    }

    public double getUpQua_V() {
        return upQua_V;
    }

    public void setUpQua_V(double upQua_V) {
        this.upQua_V = upQua_V;
    }

    public double getRangeQua_V() {
        return RangeQua_V;
    }

    public void setRangeQua_V(double RangeQua_V) {
        this.RangeQua_V = RangeQua_V;
    }

    public double getLowQua_A() {
        return lowQua_A;
    }

    public void setLowQua_A(double lowQua_A) {
        this.lowQua_A = lowQua_A;
    }

    public double getUpQua_A() {
        return upQua_A;
    }

    public void setUpQua_A(double upQua_A) {
        this.upQua_A = upQua_A;
    }

    public double getRangeQua_A() {
        return RangeQua_A;
    }

    public void setRangeQua_A(double RangeQua_A) {
        this.RangeQua_A = RangeQua_A;
    }

    public double getLowQua_TA() {
        return lowQua_TA;
    }

    public void setLowQua_TA(double lowQua_TA) {
        this.lowQua_TA = lowQua_TA;
    }

    public double getUpQua_TA() {
        return upQua_TA;
    }

    public void setUpQua_TA(double upQua_TA) {
        this.upQua_TA = upQua_TA;
    }

    public double getRangeQua_TA() {
        return RangeQua_TA;
    }

    public void setRangeQua_TA(double RangeQua_TA) {
        this.RangeQua_TA = RangeQua_TA;
    }

    public double getLowQua_Sinuosity() {
        return lowQua_Sinuosity;
    }

    public void setLowQua_Sinuosity(double lowQua_Sinuosity) {
        this.lowQua_Sinuosity = lowQua_Sinuosity;
    }

    public double getUpQua_Sinuosity() {
        return upQua_Sinuosity;
    }

    public void setUpQua_Sinuosity(double upQua_Sinuosity) {
        this.upQua_Sinuosity = upQua_Sinuosity;
    }

    public double getRangeQua_Sinuosity() {
        return RangeQua_Sinuosity;
    }

    public void setRangeQua_Sinuosity(double RangeQua_Sinuosity) {
        this.RangeQua_Sinuosity = RangeQua_Sinuosity;
    }

    public double getSkew_V() {
        return Skew_V;
    }

    public void setSkew_V(double Skew_V) {
        this.Skew_V = Skew_V;
    }

    public double getSkew_A() {
        return Skew_A;
    }

    public void setSkew_A(double Skew_A) {
        this.Skew_A = Skew_A;
    }

    public double getSkew_TA() {
        return Skew_TA;
    }

    public void setSkew_TA(double Skew_TA) {
        this.Skew_TA = Skew_TA;
    }

    public double getSkew_Sinuosity() {
        return Skew_Sinuosity;
    }

    public void setSkew_Sinuosity(double Skew_Sinuosity) {
        this.Skew_Sinuosity = Skew_Sinuosity;
    }

    public double getKurt_V() {
        return Kurt_V;
    }

    public void setKurt_V(double Kurt_V) {
        this.Kurt_V = Kurt_V;
    }

    public double getKurt_A() {
        return Kurt_A;
    }

    public void setKurt_A(double Kurt_A) {
        this.Kurt_A = Kurt_A;
    }

    public double getKurt_TA() {
        return Kurt_TA;
    }

    public void setKurt_TA(double Kurt_TA) {
        this.Kurt_TA = Kurt_TA;
    }

    public double getKurt_Sinuosity() {
        return Kurt_Sinuosity;
    }

    public void setKurt_Sinuosity(double Kurt_Sinuosity) {
        this.Kurt_Sinuosity = Kurt_Sinuosity;
    }

    public double getCV_V() {
        return CV_V;
    }

    public void setCV_V(double CV_V) {
        this.CV_V = CV_V;
    }

    public double getCV_A() {
        return CV_A;
    }

    public void setCV_A(double CV_A) {
        this.CV_A = CV_A;
    }

    public double getCV_TA() {
        return CV_TA;
    }

    public void setCV_TA(double CV_TA) {
        this.CV_TA = CV_TA;
    }

    public double getCV_Sinuosity() {
        return CV_Sinuosity;
    }

    public void setCV_Sinuosity(double CV_Sinuosity) {
        this.CV_Sinuosity = CV_Sinuosity;
    }

    public double getAutoCC_V() {
        return AutoCC_V;
    }

    public void setAutoCC_V(double AutoCC_V) {
        this.AutoCC_V = AutoCC_V;
    }

    public double getAutoCC_A() {
        return AutoCC_A;
    }

    public void setAutoCC_A(double AutoCC_A) {
        this.AutoCC_A = AutoCC_A;
    }

    public double getAutoCC_TA() {
        return AutoCC_TA;
    }

    public void setAutoCC_TA(double AutoCC_TA) {
        this.AutoCC_TA = AutoCC_TA;
    }

    public double getAutoCC_Sinuosity() {
        return AutoCC_Sinuosity;
    }

    public void setAutoCC_Sinuosity(double AutoCC_Sinuosity) {
        this.AutoCC_Sinuosity = AutoCC_Sinuosity;
    }

    public double getHCR() {
        return HCR;
    }

    public void setHCR(double HCR) {
        this.HCR = HCR;
    }

    public double getSR() {
        return SR;
    }

    public void setSR(double SR) {
        this.SR = SR;
    }

    public double getVCR() {
        return VCR;
    }

    public void setVCR(double VCR) {
        this.VCR = VCR;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }
}
