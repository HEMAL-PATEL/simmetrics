/*
 * SimMetrics - SimMetrics is a java library of Similarity or Distance Metrics,
 * e.g. Levenshtein Distance, that provide float based similarity measures
 * between String Data. All metrics return consistent measures rather than
 * unbounded similarity scores.
 * 
 * Copyright (C) 2014 SimMetrics authors
 * 
 * This file is part of SimMetrics. This program is free software: you can
 * redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * SimMetrics. If not, see <http://www.gnu.org/licenses/>.
 */
package org.simmetrics.metrics;

import java.util.Objects;

import org.simmetrics.StringMetric;
import org.simmetrics.metrics.costfunctions.MatchMismatch;
import org.simmetrics.metrics.costfunctions.Substitution;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static org.simmetrics.utils.Math.min3;


public class NeedlemanWunch implements StringMetric {

	private static final Substitution MATCH_0_MISMATCH_1 = new MatchMismatch(
			0.0f, -1.0f);

	private final Substitution substitution;

	private final float gapValue;

	public NeedlemanWunch() {
		this(-2.0f, MATCH_0_MISMATCH_1);
	}

	public NeedlemanWunch(final float gapValue, final Substitution substitution) {
		this.gapValue = gapValue;
		this.substitution = substitution;
	}

	@Override
	public float compare(String a, String b) {
		
		if (a.isEmpty() && b.isEmpty()) {
			return 1.0f;
		}

		if (a.isEmpty() || b.isEmpty()) {
			return 0.0f;
		}

		float maxDistance = max(a.length(), b.length())
				* max(substitution.max(), gapValue);
		float minDistance = max(a.length(), b.length()) * min(substitution.min(), gapValue);

		return ((-needlemanWunch(a, b) - minDistance) / (maxDistance - minDistance));

	}

	private float needlemanWunch(final String s, final String t) {

		if (Objects.equals(s, t))
			return 0;
		if (s.isEmpty()) {
			return t.length();
		}
		if (t.isEmpty()) {
			return s.length();
		}

		final int m = s.length() + 1;
		final int n = s.length() +1;
		final float[][] d = new float[m][n];

		for (int i = 1; i < m; i++) {
			d[i][0] = i;
		}
		for (int j = 1; j < n; j++) {
			d[0][j] = j;
		}

		for (int i = 1; i < m; i++) {
			for (int j = 1; j < n; j++) {
				d[i][j] = min3(
						d[i - 1][j    ] - gapValue,
						d[i    ][j - 1] - gapValue,
						d[i - 1][j - 1]
								- substitution.compare(s, i - 1, t, j - 1));
			}
		}

		return d[m - 1][n - 1];
	}

	@Override
	public String toString() {
		return "NeedlemanWunch [costFunction=" + substitution + ", gapCost="
				+ gapValue + "]";
	}

}
