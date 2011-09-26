/*
 * Copyright 2010 Daniel Kurka
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package de.kurka.phonegap.client.accelerometer.browser;

import com.google.gwt.user.client.Timer;

import de.kurka.phonegap.client.accelerometer.AccelerationCallback;
import de.kurka.phonegap.client.accelerometer.AccelerationOptions;
import de.kurka.phonegap.client.accelerometer.AccelermeterMock;
import de.kurka.phonegap.client.accelerometer.Accelerometer;
import de.kurka.phonegap.client.accelerometer.AccelerometerWatcher;

public class AccelerometerBrowserImpl implements Accelerometer, AccelermeterMock {

	@Override
	public void getCurrentAcceleration(AccelerationCallback accelerationCallback, AccelerationOptions options) {
		accelerationCallback.onSuccess(new AccelerationBrowserImpl(0, 0, 0));
	}

	@Override
	public AccelerometerWatcher watchAcceleration(final AccelerationOptions options, AccelerationCallback accelerationCallback) {
		return new AccelerometerWatcherGwtTimerImpl(options, accelerationCallback);
	}

	@Override
	public void clearWatch(AccelerometerWatcher watcher) {
		if (!(watcher instanceof AccelerometerWatcherGwtTimerImpl)) {
			throw new IllegalArgumentException("This object was not created by this implementation");
		}
		AccelerometerWatcherGwtTimerImpl accelerometerWatcherGwtTimerImpl = (AccelerometerWatcherGwtTimerImpl) watcher;
		accelerometerWatcherGwtTimerImpl.cancel();

	}

	private class AccelerometerWatcherGwtTimerImpl extends Timer implements AccelerometerWatcher {

		private final AccelerationCallback callback;
		private final AccelerationOptions options;

		public AccelerometerWatcherGwtTimerImpl(AccelerationOptions options, AccelerationCallback callback) {
			this.options = options;
			this.callback = callback;
			schedule((int) options.getFrequency());
		}

		@Override
		public void run() {

			schedule((int) options.getFrequency());

			if (shouldFail) {
				callback.onFailure();
			} else {

				if (useMockValues) {
					AccelerationBrowserImpl impl = mockValues[currentIndex];
					currentIndex++;
					currentIndex = currentIndex % maxIndex;
					callback.onSuccess(impl);
				} else {
					callback.onSuccess(new AccelerationBrowserImpl(0, 0, 0));
				}
			}

		}

	}

	private AccelerationBrowserImpl[] mockValues;
	private int currentIndex;
	private int maxIndex;
	private boolean useMockValues;

	private boolean shouldFail;

	@Override
	public void setMockValues(AccelerationBrowserImpl[] values) {
		if (values == null) {
			mockValues = null;
			useMockValues = false;
		} else {
			if (mockValues.length == 0) {
				throw new IllegalArgumentException("no values provided");
			}
			this.mockValues = values;
			this.currentIndex = 0;
			this.maxIndex = mockValues.length;
			useMockValues = true;

		}

	}

	public void setShouldFail(boolean shouldFail) {
		this.shouldFail = shouldFail;
	}

}
