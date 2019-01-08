package com.geodb.ite.iri.manager.ui.parts.providers;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import javax.inject.Singleton;

import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.jface.viewers.ColumnLabelProvider;

@Singleton
@Creatable
public class ObjectLabelProvider extends ColumnLabelProvider {

	private static final String DECIMAL_FORMAT = "#.##";
	private static final String ARRAY_SEPARATOR = ";";

	@Override
	public String getText(Object element) {
		return (element instanceof Entry<?, ?>)
				? getFieldValue(((Entry<?, ?>) element).getValue())
				: null;

	}

	private String getFieldValue(Object value) {
		if (value != null) {
			if (isDecimalArray(value)) {
				DecimalFormat decimalFormat = new DecimalFormat(DECIMAL_FORMAT);
				return Arrays.asList(toDoubleArray(value))
						.stream()
						.map(decimalFormat::format)
						.collect(Collectors.joining(ARRAY_SEPARATOR));
			} else if (isNaturalArray(value)) {
				return Arrays.asList(toNaturalArray(value))
						.stream()
						.map(v -> v.toString())
						.collect(Collectors.joining(ARRAY_SEPARATOR));
			} else if (value instanceof String[]) {
				return String.join(ARRAY_SEPARATOR, (String[]) value);
			} else if (isDecimalValue(value)) {
				DecimalFormat decimalFormat = new DecimalFormat(DECIMAL_FORMAT);
				return decimalFormat.format(value);
			} else {
				return value.toString();
			}
		}
		return null;
	}

	private static boolean isDecimalArray(Object value) {
		return (value instanceof float[]
				|| value instanceof Float[]
				|| value instanceof double[]
				|| value instanceof Double[]);
	}

	private static Double[] toDoubleArray(Object array) {
		Double[] result = null;
		if (array instanceof float[]) {
			result = new Double[((float[]) array).length];
			for (int i = 0; i < result.length; i++) {
				result[i] = Double.valueOf(((float[]) array)[i]);
			}
		} else if (array instanceof Float[]) {
			result = Arrays.asList((Float[]) array)
					.stream()
					.map(Double::valueOf)
					.toArray(Double[]::new);
		} else if (array instanceof double[]) {
			result = new Double[((double[]) array).length];
			for (int i = 0; i < result.length; i++) {
				result[i] = Double.valueOf(((double[]) array)[i]);
			}
		} else if (array instanceof Double[]) {
			result = (Double[]) array;
		}
		return result;
	}

	private static boolean isNaturalArray(Object value) {
		return (value instanceof int[]
				|| value instanceof Integer[]
				|| value instanceof long[]
				|| value instanceof Long[]);
	}

	private static Long[] toNaturalArray(Object array) {
		Long[] result = null;
		if (array instanceof int[]) {
			result = new Long[((int[]) array).length];
			for (int i = 0; i < result.length; i++) {
				result[i] = Long.valueOf(((int[]) array)[i]);
			}
		} else if (array instanceof Integer[]) {
			result = Arrays.asList((Integer[]) array)
					.stream()
					.map(Long::valueOf)
					.toArray(Long[]::new);
		} else if (array instanceof long[]) {
			result = new Long[((long[]) array).length];
			for (int i = 0; i < result.length; i++) {
				result[i] = Long.valueOf(((long[]) array)[i]);
			}
		} else if (array instanceof Long[]) {
			result = (Long[]) array;
		}
		return result;
	}

	private static boolean isDecimalValue(Object value) {
		return (value instanceof Float || value instanceof Double);
	}

}
