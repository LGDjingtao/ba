package com.subsystem.core.strategy;

import cn.hutool.core.util.NumberUtil;
import com.subsystem.core.common.Constants;
import lombok.extern.slf4j.Slf4j;

/**
 * 报警策略
 */
@Slf4j
public class AlarmStrategy {
    /**
     * @param strategy  策略
     * @param threshold 策略阈值
     * @param value     真实值
     * @return
     */
    public static boolean strategicJudgment(String strategy, String threshold, String value) {

        /**
         * 策略值 - 0
         * 返回：value = true 报警    |    value = false 不报警
         */
        if (strategy.equals("0")) {
            try {
                if (value.equals(Constants.SPECIAL_FIELDS_TRUE)) return true;
                if (value.equals(Constants.SPECIAL_FIELDS_FALSE)) return false;
                if (value.equals(Constants.SPECIAL_FIELDS_1)) return true;
                if (value.equals(Constants.SPECIAL_FIELDS_0)) return false;
            } catch (Exception e) {
                log.error("{}", e.getMessage());
                log.error("策略0->真实值或者阈值有误(真实值和阈值必须为true | false) value:{}", value);
            }
            return false;
        }

        /**
         * 策略值 - 1
         * value 高于等于 threshold
         */
        if (strategy.equals("1")) {
            //判断是否是数值
            try {
                double v = Double.parseDouble(value);
                double t = Double.parseDouble(threshold);
                int compare = NumberUtil.compare(v, t);
                //小于阈值不报警
                if (compare < 0) {
                    return false;
                }
                //大于等于阈值报警
                return true;
            } catch (Exception e) {
                log.error("阈值或数值有误，转换为数值失败");
            }
            return false;
        }

        /**
         * 策略值 - 2
         * value 低于于等于 threshold
         */
        if (strategy.equals("2")) {
            //判断是否是数值
            try {
                double v = Double.parseDouble(value);
                double t = Double.parseDouble(threshold);
                int compare = NumberUtil.compare(v, t);
                //大于阈值不报警
                if (compare > 0) {
                    return false;
                }
                //小于等于阈值报警
                return true;
            } catch (Exception e) {
                log.error("阈值或数值有误，转换为数值失败");
            }
            return false;
        }

        /**
         * 策略值 - 3
         * 返回：value = false 报警    |    value = true 不报警
         */
        if (strategy.equals("3")) {
            try {
                if (null==value)return false;
                if (value.equals(Constants.SPECIAL_FIELDS_TRUE)) return false;
                if (value.equals(Constants.SPECIAL_FIELDS_FALSE)) return true;
                if (value.equals(Constants.SPECIAL_FIELDS_1)) return false;
                if (value.equals(Constants.SPECIAL_FIELDS_0)) return true;
            } catch (Exception e) {
                log.error("{}", e.getMessage());
                log.error("策略3->真实值或者阈值有误(真实值和阈值必须为true | false | 0 | 1)value:{}", value);
            }
            return false;
        }
        return false;
    }

    /**
     * 判断策略类型是不是数值型
     */
    public static boolean isNumericalType(String strategy) {
        if (strategy.equals("0")) {
            return false;
        }
        return true;
    }
}
