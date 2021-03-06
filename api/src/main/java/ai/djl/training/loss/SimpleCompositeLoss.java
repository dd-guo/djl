/*
 * Copyright 2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"). You may not use this file except in compliance
 * with the License. A copy of the License is located at
 *
 * http://aws.amazon.com/apache2.0/
 *
 * or in the "license" file accompanying this file. This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES
 * OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 */
package ai.djl.training.loss;

import ai.djl.ndarray.NDList;
import ai.djl.util.Pair;
import java.util.ArrayList;
import java.util.List;

/**
 * {@code SimpleCompositeLoss} is an implementation of the {@link Loss} abstract class that can
 * combine different {@link Loss} functions by adding the individual losses together.
 *
 * <p>This class can be used when the losses either accept a single index of the labels and
 * predictions or the entire lists. For more complicated composite losses, extend the {@link
 * AbstractCompositeLoss}.
 */
public class SimpleCompositeLoss extends AbstractCompositeLoss {

    private List<Integer> indices;

    /**
     * Creates a new empty instance of {@code CompositeLoss} that can combine the given {@link Loss}
     * components.
     */
    public SimpleCompositeLoss() {
        this("CompositeLoss");
    }

    /**
     * Creates a new empty instance of {@code CompositeLoss} that can combine the given {@link Loss}
     * components.
     *
     * @param name the display name of the loss
     */
    public SimpleCompositeLoss(String name) {
        super(name);
        components = new ArrayList<>();
        indices = new ArrayList<>();
    }

    /**
     * Adds a Loss that applies to all labels and predictions to this composite loss.
     *
     * @param loss the loss to add
     * @return this composite loss
     */
    public SimpleCompositeLoss addLoss(Loss loss) {
        components.add(loss);
        indices.add(null);
        return this;
    }

    /**
     * Adds a Loss that applies to a single index of the label and predictions to this composite
     * loss.
     *
     * @param loss the loss to add
     * @param index the index in the label and predictions NDLists this loss applies to
     * @return this composite loss
     */
    public SimpleCompositeLoss addLoss(Loss loss, int index) {
        components.add(loss);
        indices.add(index);
        return this;
    }

    @Override
    protected Pair<NDList, NDList> inputForComponent(
            int componentIndex, NDList labels, NDList predictions) {
        if (indices.get(componentIndex) != null) {
            int index = indices.get(componentIndex);
            return new Pair<>(new NDList(labels.get(index)), new NDList(predictions.get(index)));
        } else {
            return new Pair<>(labels, predictions);
        }
    }

    /** {@inheritDoc} */
    @Override
    public Loss duplicate() {
        SimpleCompositeLoss dup = new SimpleCompositeLoss(getName());
        for (int i = 0; i < components.size(); i++) {
            if (indices.get(i) != null) {
                dup.addLoss(components.get(i).duplicate(), indices.get(i));
            } else {
                dup.addLoss(components.get(i).duplicate());
            }
        }
        return dup;
    }
}
